/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import BL.APIs.Arxiv.ArxivAPICaller;
import BL.APIs.Arxiv.ArxivAPIresponseParser;
import BL.APIs.Mendeley.ContainerMendeleyDocuments;
import BL.APIs.Mendeley.MendeleyAPICaller;
import BL.APIs.Mendeley.MendeleyAPIresponseParser;
import BL.DocumentHandling.AuthorNamesCleaner;
import BL.DocumentHandling.AuthorStatsHandler;
import BL.DocumentHandling.AuthorsExtractor;
import BL.DocumentHandling.DocsStatsHandler;
import BL.DocumentHandling.DocumentAggregator;
import BL.NameDisambiguation.FullNameInvestigator;
import BL.NameDisambiguation.SpellingDifferencesChecker;
import BL.Viz.Processing.ConvertToSegments;
import BL.Viz.Processing.Segment;
import Model.Author;
import Model.CloseMatchBean;
import Model.Document;
import Model.GlobalEditsCounter;
import Model.MapLabels;
import Model.Search;
import Utils.Clock;
import Utils.Pair;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.google.common.collect.HashMultiset;
import com.google.gson.Gson;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import java.io.BufferedReader;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    static public HashSet<Author> mostFrequentCoAuthors;
    static public int nbDocs;
    static public int minYear;
    static public int maxYear;
    static public int nbArxivDocs = 0;
    static public int nbMendeleyDocs = 0;
    BufferedReader readerArxivResults;
    static public HashMultiset<Author> multisetAuthors;
    static public Set<Author> setAuthors;
    static public HashSet<Document> setDocumentsUnFiltered;
    static public Set<Document> setDocs;
    static public TreeSet<MapLabels> setMapLabels;
    static public HashMap<String, Pair<Integer, Integer>> mapAuthorToDates;

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

        multisetAuthors = HashMultiset.create();
        setAuthors = new HashSet();
        setDocumentsUnFiltered = new HashSet();
        setDocs = new HashSet();
        setMapLabels = new TreeSet();


        System.out.println("forename: " + forename);
        System.out.println("surname: " + surname);

        //Cleans a bit the user input
        forename = forename.replaceAll("-\\.", " ").trim();
        surname = surname.replaceAll("-\\.", " ").trim();
        uuid = UUID.randomUUID();


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
        // aggregating documents from different API source: removing duplicates and incomplete records
        Clock aggregatorClock = new Clock("aggregating docs from different APIs into one single set");
        setDocs = DocumentAggregator.aggregate(setDocs);
        aggregatorClock.closeAndPrintClock();

        //3
        // extract a set of authors from the set of docs
        Clock authorExtractorClock = new Clock("extracting authors from the set of docs");
        multisetAuthors = AuthorsExtractor.extractFromSetDocs(setDocs);
        authorExtractorClock.closeAndPrintClock();


        //4
        // cleans the authors names (deletes dots, etc.)
        Clock authorCleanerClock = new Clock("cleaning authors names");
        multisetAuthors = AuthorNamesCleaner.cleanFullName(multisetAuthors);
        authorCleanerClock.closeAndPrintClock();


        //5
        //finds first and last names when they are missing
        Clock findFirstLastNamesClock = new Clock("finds first and last names in the frequent case when they are missing");
        setAuthors = FullNameInvestigator.investigate(multisetAuthors.elementSet());
        findFirstLastNamesClock.closeAndPrintClock();


        //7
        //Detects pairs of names which are probably the same person, with different spellings / misspellings
        Clock spellCheckClock = new Clock("finding possible misspellings in names");
        atleastOneMatchFound = new SpellingDifferencesChecker(setAuthors, wisdomCrowds).check();
        spellCheckClock.closeAndPrintClock();



        if (atleastOneMatchFound) {
            System.out.println("similar names found. Navigating to the spell check page");
            pageToNavigateTo = "pairscheck?faces-redirect=true";

        } else {
            System.out.println("No ambiguous name found. Navigating directly to the visualization");
            segments = new ConvertToSegments().convert();
            segments.add(new Segment(forename + " " + surname, 1, true));
            json = new Gson().toJson(segments);

            pageToNavigateTo = "report?faces-redirect=true";
        }
        return pageToNavigateTo;
    }

    public static void computationsBeforeReport() {

        //1
        //generate descriptive stats at this stage
        Clock generateStats = new Clock("generating descriptive stats on the set of authors after user input");
        setAuthors = AuthorStatsHandler.updateAuthorNamesAfterUserInput();
        DocsStatsHandler.computeNumberDocs();
        generateStats.closeAndPrintClock();



        //PERSIST SEGMENTS
        segments = new ConvertToSegments().convert();
        segments.add(new Segment(getSearch().getFullnameWithComma(), 1, true));
        setJson(new Gson().toJson(segments));
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


    public void setNbDocs(int nbDocs) {
        ControllerBean.nbDocs = nbDocs;
    }

    public int getNbDocs() {
        return nbDocs;
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

            if (newSearch.contains(
                    ",")) {
                String[] fields = newSearch.split(",");
                forename = fields[1].trim();
                surname = fields[0].trim();
            }

            map.remove(
                    "clickedAuthor");
            map.remove(
                    "uuid");

        }
    }
}
