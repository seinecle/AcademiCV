/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import BL.APIs.Mendeley.MendeleyAPICaller;
import BL.APIs.Mendeley.MendeleyDoc;
import BL.Viz.Processing.ConvertToSegments;
import BL.Viz.Processing.Segment;
import Model.Author;
import Model.CloseMatchBean;
import Model.DocumentBean;
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
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
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
    static public DocumentBean mendeleyDocs;
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
    private int count;

    @PostConstruct
    private void init() {
        try {
            Mongo m;
            m = new Mongo();
            Morphia morphia = new Morphia();
            morphia.map(Author.class);
            morphia.map(MendeleyDoc.class);
            ds = morphia.createDatastore(m, "namesDB");
            count = ds.find(GlobalEditsCounter.class).get().getGlobalCounter();


        } catch (UnknownHostException ex) {
            Logger.getLogger(ControllerBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MongoException ex) {
            Logger.getLogger(ControllerBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void persistDocsToMorphia(List<MendeleyDoc> mendeleyDocs) {
        Iterator<MendeleyDoc> mendeleyDocsIterator = mendeleyDocs.iterator();
        int docCount = 0;
        MendeleyDoc currDoc;
        while (mendeleyDocsIterator.hasNext()) {
            docCount++;
            currDoc = mendeleyDocsIterator.next();
            currDoc.setUuid(ControllerBean.uuid);
            ds.save(currDoc);
        }
        System.out.println(docCount + " documents persisted with Morphia.");

    }

    public String launchNewSearch() throws Exception {

        System.out.println("forename: " + forename);
        System.out.println("surname: " + surname);

        //Cleans a bit the user input
        forename = forename.replaceAll("-\\.", " ").trim();
        surname = surname.replaceAll("-\\.", " ").trim();
        uuid = UUID.randomUUID();


        //DELETING RECORDS CONNECTED TO THIS UUID;
        Query q1 = ds.createQuery(MendeleyDoc.class).field("uuid").equal(uuid.toString());
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



        //1
        Clock gettingMendeleyData = new Clock("calling Mendeley...");
        mendeleyDocs = MendeleyAPICaller.run(forename, surname);
        gettingMendeleyData.closeAndPrintClock();
        System.out.println("phase 1 passed: API call and response are OK");

        //2
        Clock persistDocsMorphia = new Clock("persisting docs to Morphia");
        persistDocsToMorphia(mendeleyDocs.getDocuments());
        System.out.println("phase 2 passed: API response is persisted");
        persistDocsMorphia.closeAndPrintClock();

        //3
        Clock spellCheckClock = new Clock("finding possible misspellings in names");
        atleastOneMatchFound = new SpellingDifferencesChecker(forename, surname, wisdomCrowds).doAll();
        System.out.println("phase 3 passed: potential misspellings identified");
        spellCheckClock.closeAndPrintClock();



        if (atleastOneMatchFound) {
            System.out.println("close matches found. Navigating to the spell check page");
            pageToNavigateTo = "show";

        } else {
            System.out.println("No ambiguous name found. Navigating directly to the visualization");
            List listMapLabels = ControllerBean.ds.find(MapLabels.class).field("uuid").equal(ControllerBean.uuid.toString()).asList();
            segments = ConvertToSegments.convert(listMapLabels);
            segments.add(new Segment(forename + " " + surname, 1, true));
            json = new Gson().toJson(segments);

            pageToNavigateTo = "processing";
        }
        return pageToNavigateTo;
    }

    public static void transformToJson() {

//        System.out.println("returning json");
        segments = (ArrayList<Segment>) ds.find(Segment.class).field("uuid").equal(uuid.toString()).asList();
        System.out.println("returning json // segments size is: " + segments.size());

        segments.add(new Segment(search.getFullnameWithComma(), 1, true));
        json = new Gson().toJson(segments);
//        System.out.println("json: " + json);
    }

    public static String getJson() {
        return json;
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
        System.out.println("getting the boolean!");
        return wisdomCrowds;
    }

    public void setWisdomCrowds(boolean wisdomCrowds) {
        this.wisdomCrowds = wisdomCrowds;
        System.out.println("setting the boolean!");
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public static synchronized void pushCounter() {
        int count = ds.find(GlobalEditsCounter.class).get().getGlobalCounter();
        System.out.println("counter in pushcCounter method is:" + count);
        PushContext pushContext = PushContextFactory.getDefault().getPushContext();
        pushContext.push("/counter", String.valueOf(count));
    }
}
