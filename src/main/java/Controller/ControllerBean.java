/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import BL.APIs.Arxiv.ArxivAPICaller;
import BL.APIs.Arxiv.ArxivAPIresponseParser;
import BL.APIs.Mendeley.MendeleyAPICaller;
import Model.Document;
import BL.APIs.Mendeley.MendeleyDocsGetAuthors;
import BL.Viz.Processing.ConvertToSegments;
import BL.Viz.Processing.Segment;
import Model.Author;
import Model.CloseMatchBean;
import BL.APIs.Mendeley.ContainerMendeleyDocuments;
import BL.APIs.Mendeley.MendeleyAPIresponseParser;
import BL.DocumentAggregation.Aggregator;
import Model.GlobalEditsCounter;
import Model.MapLabels;
import Model.Search;
import Utils.Clock;
import Utils.SpellingDifferencesChecker;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.google.gson.Gson;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import java.io.BufferedReader;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.push.PushContext;
import org.primefaces.push.PushContextFactory;

/**
 *
 * @author C. Levallois
 */
@ManagedBean
@SessionScoped
public class ControllerBean implements Serializable {

    DBCollection quidamDocsColl;
    DBCollection quidamPartnersColl;
    static public Datastore ds;
    static public TreeSet<CloseMatchBean> setCloseMatches;
    List<CloseMatchBean> listCloseMatches;
    static public ContainerMendeleyDocuments mendeleyDocs;
    public static TreeMap<Author, Author> mapCloseMatches = new TreeMap();
    private String forename;
    private String surname;
    public boolean wisdomCrowds;
    static private String json;
    static public UUID uuid;
    static public boolean atleastOneMatchFound;
    static private ArrayList<Segment> segments;
    static String pageToNavigateTo;
    static private Search search;
    static private int count;
    static public TreeSet<Author> authorsInMendeleyDocs;
    static public Author mostFrequentCoAuthor;
    static public int nbMendeleyDocs;
    static public int minYear;
    static public int maxYear;
    static public HashSet<Document> setDocumentsUnFiltered = new HashSet();
    BufferedReader readerArxivResults;

    @PostConstruct
    private void init() {
        try {
            Mongo m;
            m = new Mongo();
            Morphia morphia = new Morphia();
            morphia.map(Author.class);
            morphia.map(Document.class);
            ds = morphia.createDatastore(m, "namesDB");
            count = ds.find(GlobalEditsCounter.class).get().getGlobalCounter();
            pushCounter();


        } catch (UnknownHostException ex) {
            Logger.getLogger(ControllerBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MongoException ex) {
            Logger.getLogger(ControllerBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public String launchNewSearch() throws Exception {

        System.out.println("forename: " + forename);
        System.out.println("surname: " + surname);

        //Cleans a bit the user input
        forename = forename.replaceAll("-\\.", " ").trim();
        surname = surname.replaceAll("-\\.", " ").trim();
        uuid = UUID.randomUUID();


        //DELETING RECORDS CONNECTED TO THIS UUID;
        Query q1 = ds.createQuery(Document.class).field("uuid").equal(uuid.toString());
        Query q2 = ds.createQuery(MapLabels.class).field("uuid").equal(uuid.toString());
        Query q3 = ds.createQuery(Segment.class).field("uuid").equal(uuid.toString());
        ds.delete(q1);
        ds.delete(q2);
        ds.delete(q3);

        //PERSISTING THE MAIN FORENAME AND SURNAME;
        search = new Search();
        search.setDate();
        search.setForename(forename);
        search.setSurname(surname);
        search.setUuid(uuid.toString());
        ds.save(search);
        search = ds.find(Search.class).field("uuid").equal(uuid.toString()).get();


        //0
        // Calling the Arxiv database and persisting the docs in a standardized form
        Clock gettingArxivData = new Clock("calling Arxiv...");
        if (AdminPanel.arxivDebugStateTrueOrFalse()) {
            new ArxivAPIresponseParser().parse();
        } else {
            readerArxivResults = ArxivAPICaller.run(forename, surname);
            new ArxivAPIresponseParser(readerArxivResults).parse();
        }
        gettingArxivData.closeAndPrintClock();


        //1
        // Calling the Mendeley database and persisting the docs in a standardized form
        Clock gettingMendeleyData = new Clock("calling Mendeley...");

        mendeleyDocs = MendeleyAPICaller.run(forename, surname);
        new MendeleyAPIresponseParser(mendeleyDocs).parse();

        gettingMendeleyData.closeAndPrintClock();

        //2
        Aggregator.aggregate();

        //3
        Clock spellCheckClock = new Clock("finding possible misspellings in names");
        //authorsInMendeleyDocs = new MendeleyDocsGetAuthors().countAuthors(mendeleyDocs.getDocuments(), forename, surname);
        createAuthorsStats();
        atleastOneMatchFound = new SpellingDifferencesChecker(forename, surname, authorsInMendeleyDocs, wisdomCrowds).doAll();
        System.out.println("phase 3 passed: potential misspellings identified");
        spellCheckClock.closeAndPrintClock();



        if (atleastOneMatchFound) {
            System.out.println("close matches found. Navigating to the spell check page");
            pageToNavigateTo = "pairscheck?faces-redirect=true";

        } else {
            System.out.println("No ambiguous name found. Navigating directly to the visualization");
            List<MapLabels> listMapLabels = ControllerBean.ds.find(MapLabels.class).field("uuid").equal(ControllerBean.uuid.toString()).asList();
            TreeMap<String, String> mapLabelsAuthors = new TreeMap();
            for (MapLabels element : listMapLabels) {
                mapLabelsAuthors.put(element.getLabel1(), element.getLabel2());
            }
            segments = new ConvertToSegments().convert(mapLabelsAuthors);
            segments.add(new Segment(forename + " " + surname, 1, true));
            json = new Gson().toJson(segments);

            pageToNavigateTo = "report?faces-redirect=true";
        }
        return pageToNavigateTo;
    }

    public static void transformToJson(ArrayList<Segment> segments) {

//        System.out.println("returning json");
//        segments = (ArrayList<Segment>) ds.find(Segment.class).field("uuid").equal(uuid.toString()).asList();
//        System.out.println("returning json // segments size is: " + segments.size());

        segments.add(new Segment(search.getFullnameWithComma(), 1, true));
        json = new Gson().toJson(segments);
//        System.out.println("json: " + json);
    }

    public static String getJson() {
        return json;
    }

    public static void setJson(String newJson) {
        json = newJson;
    }

    public static boolean isAtleastOneMatchFound() {
        return atleastOneMatchFound;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = StringUtils.capitalize(forename);
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = StringUtils.capitalize(surname);
    }

    public static Search getSearch() {
        return search;
    }

    public boolean isWisdomCrowds() {
        return wisdomCrowds;
    }

    public void setWisdomCrowds(boolean wisdomCrowds) {
        this.wisdomCrowds = wisdomCrowds;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public static synchronized void pushCounter() {
//        int count = ds.find(GlobalEditsCounter.class).get().getGlobalCounter();
//        System.out.println("counter in pushCounter method in ControllerBean is:" + count);
//        PushContext pushContext = PushContextFactory.getDefault().getPushContext();
//        pushContext.push("/counter", String.valueOf(count).trim());
//        System.out.println("string value of count: ");
//        System.out.println("string value of count: " + String.valueOf(count).trim());
        count = ds.find(GlobalEditsCounter.class).get().getGlobalCounter();
    }

    private void createAuthorsStats() {
        Iterator<Author> authorsInMendeleyDocsIterator = authorsInMendeleyDocs.iterator();
        int times = 0;
        Author currCoAuthor;
        while (authorsInMendeleyDocsIterator.hasNext()) {
            currCoAuthor = authorsInMendeleyDocsIterator.next();
            if (currCoAuthor.getTimesMentioned() > times) {
                mostFrequentCoAuthor = currCoAuthor;
                times = currCoAuthor.getTimesMentioned();
            }
        }
    }

    public Author getMostFrequentCoAuthor() {
        return mostFrequentCoAuthor;
    }

    public void setNbMendeleyDocs(int nbMendeleyDocs) {
        ControllerBean.nbMendeleyDocs = nbMendeleyDocs;
    }

    public int getNbMendeleyDocs() {
        return nbMendeleyDocs;
    }

    public static int getMinYear() {
        return minYear;
    }

    public static void setMinYear(int minYear) {
        ControllerBean.minYear = minYear;
    }

    public static int getMaxYear() {
        return maxYear;
    }

    public static void setMaxYear(int maxYear) {
        ControllerBean.maxYear = maxYear;
    }

    public static TreeSet<Author> getAuthorsInMendeleyDocs() {
        return authorsInMendeleyDocs;
    }

    public void prepareNewSearch() {
        Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getApplicationMap();

        if (!map.isEmpty()) {

            String previousUUID = (String) map.get("uuid");
            Query q1 = ds.createQuery(Document.class).field("uuid").equal(previousUUID);
            Query q2 = ds.createQuery(MapLabels.class).field("uuid").equal(previousUUID);
            Query q3 = ds.createQuery(Segment.class).field("uuid").equal(previousUUID);
            ds.delete(q1);
            ds.delete(q2);
            ds.delete(q3);


            String newSearch = (String) map.get("clickedAuthor");
            if (newSearch.contains(",")) {
                String[] fields = newSearch.split(",");
                forename = fields[1].trim();
                surname = fields[0].trim();
            }

            map.remove("clickedAuthor");
            map.remove("uuid");

        }
    }
}
