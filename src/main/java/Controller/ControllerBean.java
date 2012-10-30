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
import BL.APIs.NYT.ContainerNYTDocuments;
import BL.APIs.NYT.NYTAPICaller;
import BL.APIs.WorldCatIdentities.WorldCatAPIController;
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
import Model.PersistingAcademic;
import Model.PersistingEdit;
import Model.PersistingFeedback;
import Utils.Clock;
import Utils.Pair;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.common.collect.HashMultiset;
import com.google.gson.Gson;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.InputSource;

/**
 *
 * @author C. Levallois
 */
@ManagedBean
@SessionScoped
public class ControllerBean implements Serializable {

    private static Pair<String, Integer> mostFreqSource;
    private static Query<PersistingAcademic> updateQueryPA;
    private static UpdateOperations<PersistingAcademic> opsPA;
    DBCollection quidamDocsColl;
    DBCollection quidamPartnersColl;
    static public Datastore ds;
    static public TreeSet<CloseMatchBean> setCloseMatches;
    List<CloseMatchBean> listCloseMatches;
    static public ContainerMendeleyDocuments mendeleyDocs;
    static public ContainerNYTDocuments NYTDocs;
    public static TreeMap<Author, Author> mapCloseMatches;
    private String forename;
    private String surname;
    public boolean wisdomCrowds = true;
    static private String json;
    static public UUID uuid;
    static public boolean atleastOneMatchFound;
    static private ArrayList<Segment> segments;
    static String pageToNavigateTo;
    static private Author search;
    static private int count;
    static public HashSet<Author> mostFrequentCoAuthors;
    static public int nbDocs;
    static public int minYear;
    static public int maxYear;
    static public int nbArxivDocs = 0;
    static public int nbMendeleyDocs = 0;
    static public HashMultiset<Author> multisetAuthors;
    static public Set<Author> setAuthors;
    static public HashSet<Document> setDocumentsUnFiltered;
    static public Set<Document> setDocs;
    static public TreeSet<MapLabels> setMapLabels;
    static public HashMap<String, Pair<Integer, Integer>> mapAuthorToDates;
    private boolean NYTfound;
    private Query<GlobalEditsCounter> updateQueryCounter;
    private UpdateOperations<GlobalEditsCounter> opsCounter;
    static private int tempBirthYear = 0;
    static private boolean coAuthorsFound = true;
    private String feedback;
    private int progress;

    @PostConstruct
    private void init() {
        try {
            AdminPanel.setLocalMode(AdminPanel.isDebug_local());
            Mongo m;
            Morphia morphia;


            //JELASTIC SETTINGS  
//            Properties prop = new Properties();
//            prop.load(new FileInputStream(System.getProperty("user.home") + "/mydb.cfg"));
//            String host = prop.getProperty("host").toString();
//            String dbname = prop.getProperty("dbname").toString();
//            String user = prop.getProperty("user").toString();
//            String password = prop.getProperty("password").toString();
//            System.out.println("host: " + host + "\ndbname: " + dbname + "\nuser: " + user + "\npassword: " + password);
//
//            m = new Mongo(host, 27017);
//            DB db = m.getDB(dbname);
//            if (db.authenticate(user, password.toCharArray())) {
//                System.out.println("Connected!");
//            } else {
//                System.out.println("Connection failed");
//            }

            if (AdminPanel.isDebug_local()) {
//             LOCAL SETTINGS
                m = new Mongo();
                morphia = new Morphia();
                ds = morphia.createDatastore(m, "test");

//
            } else {
                //CLOUDBEES SETTINGS
                m = new Mongo("alex.mongohq.com", 10003);
                morphia = new Morphia();
                ds = morphia.createDatastore(m, Utils.APIkeys.getMongoHQDatabaseName(), "seinecle", Utils.APIkeys.getMongoHQDatabasePassWord().toCharArray());
                if (ds != null) {
                    System.out.println("Morphia datastore on CloudBees / MongoHQ created!!!!!!!");
                }
            }

            morphia.map(GlobalEditsCounter.class);
            morphia.map(PersistingAcademic.class);
            morphia.map(PersistingEdit.class);
            morphia.map(PersistingFeedback.class);

            GlobalEditsCounter gec = ds.find(GlobalEditsCounter.class).get();
            if (gec == null) {
                count = 0;
                updateQueryCounter = ds.createQuery(GlobalEditsCounter.class);
                opsCounter = ds.createUpdateOperations(GlobalEditsCounter.class).inc("globalCounter", 1);
                ds.update(updateQueryCounter, opsCounter, true);

            } else {
                count = gec.getGlobalCounter();
            }
            pushCounter();


        } catch (UnknownHostException ex) {
            Logger.getLogger(ControllerBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MongoException ex) {
            Logger.getLogger(ControllerBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("IO exception: " + ex);
        }
    }

    public String launchNewSearch() throws Exception {

        multisetAuthors = HashMultiset.create();
        setAuthors = new HashSet();
        setDocumentsUnFiltered = new HashSet();
        setDocs = new HashSet();
        setMapLabels = new TreeSet();
        mapCloseMatches = new TreeMap();
        setCloseMatches = new TreeSet();
        mapAuthorToDates = new HashMap();
        setDocumentsUnFiltered = new HashSet();
        segments = new ArrayList();
        mendeleyDocs = null;
        NYTDocs = null;
        nbMendeleyDocs = 0;
        nbArxivDocs = 0;
        setProgress(50);


        //Cleans a bit the user input
        forename = forename.replaceAll("\\.|\"", " ").trim();
        surname = surname.replaceAll("\\.|\"", " ").trim();
        forename = forename.replaceAll("  ", " ");
        surname = surname.replaceAll("  ", " ");

        System.out.println(
                "forename: " + forename);
        System.out.println(
                "surname: " + surname);

        uuid = UUID.randomUUID();
        //PERSISTING THE MAIN FORENAME AND SURNAME;
        search = new Author();
        search.setForename(forename);
        search.setSurname(surname);
        search.setUuid(uuid.toString());


        //-3
        // Calling the SCOPUS database
        //WORK IN PROGRESS - GET AN HTML ERROR CODE OF 401 AT THE MOMENT
        //ScopusAPICaller.run(forename, surname);

        //-2
        // Calling the WORLDCAT database
        Clock gettingWorldCat = new Clock("calling WorldCat...");
        WorldCatAPIController worldcat = new WorldCatAPIController();
        worldcat.run();
        gettingWorldCat.closeAndPrintClock();
        setProgress(10);

        //0
        // Calling the Arxiv database and persisting the docs in a standardized form
        Clock gettingArxivData = new Clock("calling Arxiv...");
        InputSource readerArxivResults = ArxivAPICaller.run(forename, surname);
        new ArxivAPIresponseParser(readerArxivResults).parse();
        System.out.println("nb Arxiv docs found: " + nbArxivDocs);
        gettingArxivData.closeAndPrintClock();
        setProgress(20);


        //1
        // Calling the Mendeley API and persisting the docs in a standardized form
        Clock gettingMendeleyData = new Clock("calling Mendeley...");
        mendeleyDocs = MendeleyAPICaller.run(forename, surname);
        new MendeleyAPIresponseParser(mendeleyDocs).parse();
        System.out.println("nb Mendeley docs found: " + nbMendeleyDocs);

        gettingMendeleyData.closeAndPrintClock();
        setProgress(30);

        //2
        // Calling the NYT API
        Clock gettingNYTData = new Clock("calling the NYT...");
        NYTDocs = NYTAPICaller.callAPI(forename, surname);
        NYTfound = !NYTDocs.getDocuments().isEmpty();
        gettingNYTData.closeAndPrintClock();

        //3
        // aggregating documents from different API source: removing duplicates and incomplete records
        Clock aggregatorClock = new Clock("aggregating docs from different APIs into one single set");
        setDocs = DocumentAggregator.aggregate(setDocs);
        aggregatorClock.closeAndPrintClock();


        //4
        // NO DOCS FOUND? navigating to an error page
        if (setDocs.isEmpty()) {
            return pageToNavigateTo = "noDocFound?faces-redirect=true";
        }


        //5
        // extract a set of authors from the set of docs
        Clock authorExtractorClock = new Clock("extracting authors from the set of docs");
        multisetAuthors = AuthorsExtractor.extractFromSetDocs(setDocs);
        authorExtractorClock.closeAndPrintClock();


        //6
        // cleans the authors names (deletes dots, etc.)
        Clock authorCleanerClock = new Clock("cleaning authors names");
        multisetAuthors = AuthorNamesCleaner.cleanFullName(multisetAuthors);
        authorCleanerClock.closeAndPrintClock();


        //7
        //MORE THAN 300 CO-AUTHORS FOUND? moving to an error page
        if (multisetAuthors.elementSet().size() > 250) {
            return pageToNavigateTo = "tooManyCoAuthors?faces-redirect=true";
        }


        //8
        //finds first and last names when they are missing
        Clock findFirstLastNamesClock = new Clock("finds first and last names in the frequent case when they are missing");
        setAuthors = FullNameInvestigator.investigate(multisetAuthors.elementSet());
        findFirstLastNamesClock.closeAndPrintClock();


        //8 bis
        //extracts the author being currently researched from the set of Authors and puts it in a field in the controllerBean: currSearch
        AuthorsExtractor.extractCurrSearchedAuthor();
        search.setBirthYear(tempBirthYear);


        //9
        //Detects pairs of names which are probably the same person, with different spellings / misspellings
        Clock spellCheckClock = new Clock("finding possible misspellings in names");
        atleastOneMatchFound = new SpellingDifferencesChecker(setAuthors, wisdomCrowds).check();
        spellCheckClock.closeAndPrintClock();

        //10
        //navigates to the pages for name disambiguation or directly to the last report page
        if (atleastOneMatchFound) {
            System.out.println("Co-authors found!");
            System.out.println("Similar names found");
            System.out.println("Navigating to the spell check page");
            pageToNavigateTo = "pairscheck?faces-redirect=true";

        } else {
            if (setAuthors.size() > 0) {
                System.out.println("Co-authors found!");
                System.out.println("No similarity found between pairs of names");
                System.out.println("Navigating directly to the final check page");
                computationsBeforeReport();
                persistAcademic();
                pageToNavigateTo = "finalcheck?faces-redirect=true";
            } else {
                coAuthorsFound = false;
                System.out.println("No co-author found");
                System.out.println("Navigating directly to the report page");
                computationsBeforeReport();
                persistAcademic();

                pageToNavigateTo = "report?faces-redirect=true";
            }
        }

//            computationsBeforeReport();
//            AuthorStatsHandler.updateAuthorNamesAfterUserInput();
//            segments = new ConvertToSegments().convert();
//            segments.add(new Segment(forename + " " + surname, 1, true));
//            json = new Gson().toJson(segments);
//
//            pageToNavigateTo = "report?faces-redirect=true";

        return pageToNavigateTo;
    }

    public static void computationsBeforeReport() {

        //1
        //generate descriptive stats at this stage
        Clock generateStats = new Clock("generating descriptive stats on the set of authors after user input");
        setAuthors = AuthorStatsHandler.updateAuthorNamesAfterUserInput();
        DocsStatsHandler.computeNumberDocs();
        mostFreqSource = DocsStatsHandler.extractMostFrequentSource();
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

    public static void setSearch(Author newAuthor) {
        search = newAuthor;
    }

    public static Author getSearch() {
        return search;
    }

    public boolean isWisdomCrowds() {
        return wisdomCrowds;
    }

    public void setWisdomCrowds(boolean wisdomCrowds) {
        this.wisdomCrowds = wisdomCrowds;
    }

    public static int getCount() {
        return count;
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

    public boolean isNYTfound() {
        return NYTfound;
    }

    public static Pair<String, Integer> getMostFreqSource() {
        return mostFreqSource;
    }

    public static int getTempBirthYear() {
        return tempBirthYear;
    }

    public static void setTempBirthYear(int newTempBirthYear) {
        tempBirthYear = newTempBirthYear;
    }

    public static String wereThereCoAuthorsFound() {
        if (!coAuthorsFound) {
            return "display:none; ";
        } else {
            return "";
        }
    }

    public static TreeSet<CloseMatchBean> getSetCloseMatches() {
        return setCloseMatches;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void prepareNewSearch() {
        Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getApplicationMap();

        if (!map.isEmpty()) {

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

    public static void persistAcademic() {
        PersistingAcademic pa = new PersistingAcademic();
        pa.setFullNameWithComma(search.getFullnameWithComma());
        pa.setBirthYear(search.getBirthYear());
        pa.setCountCoAuthors(setAuthors.size());
        pa.setCountDocuments(setDocs.size());
        pa.setSetAffiliations(search.getSetAffiliations());

        updateQueryPA = ControllerBean.ds.createQuery(PersistingAcademic.class).field("fullNameWithComma").equal(search.getFullnameWithComma());
        opsPA = ControllerBean.ds.createUpdateOperations(PersistingAcademic.class).inc("searchCount", 1);
        ControllerBean.ds.update(updateQueryPA, opsPA, true);
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
        StringBuilder toBePersisted = new StringBuilder();
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String userAgent = externalContext.getRequestHeaderMap().get("User-Agent");
        if (search != null) {
            toBePersisted.append("\nSEARCH:");
            toBePersisted.append("\n");
            toBePersisted.append(ControllerBean.getSearch().getFullnameWithComma());
        }
        toBePersisted.append("\nBROWSER: ");
        toBePersisted.append("\n");
        toBePersisted.append(userAgent);
        toBePersisted.append("\nCURRENT PAGE: ");
        toBePersisted.append("\n");
        toBePersisted.append(FacesContext.getCurrentInstance().getViewRoot().getViewId());
        toBePersisted.append("\nCOMMENT: ");
        toBePersisted.append("\n");
        toBePersisted.append(this.feedback);
        PersistingFeedback pf = new PersistingFeedback();
        pf.setComment(toBePersisted.toString());
        ControllerBean.ds.save(pf);
        System.out.println("feedback persisted: " + toBePersisted.toString());

    }
}
