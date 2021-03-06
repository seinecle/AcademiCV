/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import BL.APIs.Arxiv.ArxivAPIController;
import BL.APIs.Mendeley.MendeleyAPIController;
import BL.APIs.NYT.NYTAPIController;
import BL.APIs.WorldCatIdentities.WorldCatAPIController;
import BL.DocumentHandling.AuthorNamesCleaner;
import BL.DocumentHandling.AuthorStatsHandler;
import BL.DocumentHandling.AuthorsExtractor;
import BL.DocumentHandling.DocsStatsHandler;
import BL.DocumentHandling.DocumentAggregator;
import BL.NameDisambiguation.AuthorSpellingEditor;
import BL.NameDisambiguation.CloseMatchesDetector;
import BL.NameDisambiguation.FullNameInvestigator;
import BL.NameDisambiguation.MapLabelsInitiator;
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
import Utils.PairSimple;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.common.collect.HashMultiset;
import com.google.gson.Gson;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author C. Levallois
 */
@ManagedBean
@SessionScoped
public class ControllerBean implements Serializable {

    private Query<PersistingAcademic> updateQueryPA;
    private UpdateOperations<PersistingAcademic> opsPA;
    DBCollection quidamDocsColl;
    DBCollection quidamPartnersColl;
    public static Datastore ds;
    private Set<CloseMatchBean> setCloseMatches;
    List<CloseMatchBean> listCloseMatches;
    public TreeMap<Author, Author> mapCloseMatches;
    private String forename;
    private String surname;
    public boolean wisdomCrowds = true;
    private String json;
    public UUID uuid;
    public boolean atleastOneMatchFound;
    private ArrayList<Segment> segments;
    String pageToNavigateTo;
    private Author search;
    private int count;
    private int minYear;
    private int maxYear;
    private HashMultiset<Author> multisetAuthors;
    private Set<Author> setAuthors;
    private Set<Document> setDocs;
    private TreeSet<MapLabels> setMapLabels;
    private boolean NYTfound;
    private Query<GlobalEditsCounter> updateQueryCounter;
    private UpdateOperations<GlobalEditsCounter> opsCounter;
    private int tempBirthYear = 0;
    private boolean coAuthorsFound = true;
    private String feedback;
    private List<Callable<PairSimple<Set<Document>, Author>>> calls;
    private Callable<PairSimple<Set<Document>, Author>> worldcatCallable;
    private Callable<PairSimple<Set<Document>, Author>> arxivCallable;
    private Callable<PairSimple<Set<Document>, Author>> mendeleyCallable;
    private Callable<PairSimple<Set<Document>, Author>> nytCallable;
    private Set<Document> setMediaDocs;
    private MongoClient m;
    private Morphia morphia;

    @PostConstruct
    private void init() {
        try {
            AdminPanel.setLocalMode(AdminPanel.isDebug_local());

            if (AdminPanel.isDebug_local()) {
//             LOCAL SETTINGS
                m = new MongoClient();
                morphia = new Morphia();
                ds = morphia.createDatastore(m, "test");

//
            } else {
                //SERVRR SETTINGS
                m = new MongoClient("199.59.247.173", 27017);
                morphia = new Morphia();
                ds = morphia.createDatastore(m, "academiCV");
                if (ds != null) {
                    System.out.println("Morphia datastore created on server");
                }
            }

            morphia.map(GlobalEditsCounter.class);
            morphia.map(PersistingAcademic.class);
            morphia.map(PersistingEdit.class);
            morphia.map(PersistingFeedback.class);

            GlobalEditsCounter globalEditCounter = ds.find(GlobalEditsCounter.class).get();
            if (globalEditCounter == null) {
                count = 0;
                updateQueryCounter = ds.createQuery(GlobalEditsCounter.class);
                opsCounter = ds.createUpdateOperations(GlobalEditsCounter.class).inc("globalCounter", 1);
                ds.update(updateQueryCounter, opsCounter, true);

            } else {
                count = globalEditCounter.getGlobalCounter();
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
        setMediaDocs = new HashSet();
        calls = new ArrayList<Callable<PairSimple<Set<Document>, Author>>>();
        multisetAuthors = HashMultiset.create();
        setAuthors = new HashSet();
//        setDocs = new TreeSet<Document>(new yearPublicationComparator());
        setDocs = new TreeSet();
        setMapLabels = new TreeSet();
        mapCloseMatches = new TreeMap();
        setCloseMatches = new TreeSet();
        segments = new ArrayList();


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
        // Calling all APIS
        worldcatCallable = new WorldCatAPIController(search);
        arxivCallable = new ArxivAPIController(search);
        mendeleyCallable = new MendeleyAPIController(search);
        nytCallable = new NYTAPIController(search);
        calls.add(worldcatCallable);
        calls.add(arxivCallable);
        calls.add(mendeleyCallable);
        calls.add(nytCallable);

        return "progressBar1?faces-redirect=true";
    }

    public String treatmentAPIresults() {
        Clock aggregatorClock = new Clock("aggregating docs from different APIs into one single set");
        setDocs = new DocumentAggregator().aggregate(setDocs);
        aggregatorClock.closeAndPrintClock();

        //4
        // NO DOCS FOUND? navigating to an error page
        if (setDocs.isEmpty()) {
            return pageToNavigateTo = "noDocFound?faces-redirect=true";
        }
        //5
        // extract a set of authors from the set of docs
        Clock authorExtractorClock = new Clock("extracting authors from the set of docs");
        multisetAuthors = new AuthorsExtractor().extractFromSetDocs(setDocs);
        authorExtractorClock.closeAndPrintClock();

        //6
        // cleans the authors names (deletes dots, etc.)
        Clock authorCleanerClock = new Clock("cleaning authors names");
        multisetAuthors = new AuthorNamesCleaner().cleanFullName(multisetAuthors);
        authorCleanerClock.closeAndPrintClock();

        //7
        //MORE THAN 300 CO-AUTHORS FOUND? moving to an error page
        if (multisetAuthors.elementSet().size() > 250) {
            return pageToNavigateTo = "tooManyCoAuthors?faces-redirect=true";
        }
        //8
        //finds first and last names when they are missing
        Clock findFirstLastNamesClock = new Clock("finds first and last names in the frequent case when they are missing");
        setAuthors = new FullNameInvestigator().investigate(multisetAuthors.elementSet());
        Iterator<Author> setAuthorsIterator = setAuthors.iterator();
        while (setAuthorsIterator.hasNext()) {
            Author author = setAuthorsIterator.next();
            if (!author.getFullnameWithComma().contains(",")) {
                System.out.println("author without comma: " + author.getFullnameWithComma());
            }
        }
        findFirstLastNamesClock.closeAndPrintClock();


        //8 bis
        //extracts the author being currently researched from the set of Authors and puts it in a field in the controllerBean: currSearch
        AuthorsExtractor authorsExtractor = new AuthorsExtractor();
        search = authorsExtractor.extractCurrSearchedAuthor(setAuthors, search);
        search.setNumberOfDocs(setDocs.size());


        //9
        //Detects pairs of names which are probably the same person, with different spellings / misspellings
        Clock wotcClock = new Clock("bring edits from the wisdom of the crowds to author names");
        try {
            setAuthors = new AuthorSpellingEditor(setAuthors, wisdomCrowds, search).check();
            System.out.println("number of authors found before processing: " + setAuthors.size());
        } catch (IOException ex) {
            Logger.getLogger(ControllerBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        wotcClock.closeAndPrintClock();
        
        Clock closeMatchesClock = new Clock("detecting close matches in author names, and making suggestions for their resolution");
        setCloseMatches = new CloseMatchesDetector().check(setAuthors);
        System.out.println("size of setCloseMatches after close Matches detector: " + setCloseMatches.size());
        closeMatchesClock.closeAndPrintClock();
        
        
        //this whole step here looks silly.
        //the setMapLabels does not need to exist now... not sure it needs to exist after?
        Clock initializingMapLabelsClock = new Clock("initializing a map of original author names and their correct version");
        setMapLabels = new MapLabelsInitiator().check(setAuthors, setCloseMatches, search);
        System.out.println("size of setMapLabels: " + setMapLabels.size());
        initializingMapLabelsClock.closeAndPrintClock();

        //10
        //navigates to the pages for name disambiguation or directly to the last report page
        if (!setCloseMatches.isEmpty()) {
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
        return pageToNavigateTo;
    }

    public void computationsBeforeReport() {

        //1
        //generate descriptive stats at this stage
        Clock generateStats = new Clock("generating descriptive stats on the set of authors after user input");
        setAuthors = new AuthorStatsHandler().updateAuthorNamesAfterUserInput(setDocs, setMapLabels, search);
        search = new AuthorStatsHandler().findMostFrequentCoauthor(setDocs, setAuthors, search);
        DocsStatsHandler docsStatsHandler = new DocsStatsHandler();
        search.setMostFrequentOutlet(docsStatsHandler.extractMostFrequentSource(setDocs));
        search.setNumberCoAuthors(setAuthors.size());
        generateStats.closeAndPrintClock();



        //PERSIST SEGMENTS
        //Why do we need setMapLabels here? The setAuthors is uptodate...
        segments = new ConvertToSegments().convert(setAuthors);
        segments.add(new Segment(search.getFullnameWithComma(), 1, true));
        setJson(new Gson().toJson(segments));
    }

    public void transformToJson(ArrayList<Segment> segments) {

        segments.add(new Segment(search.getFullnameWithComma(), 1, true));
        json = new Gson().toJson(segments);
    }

    public String getJson() {
        return json;
    }

    public void setJson(String newJson) {
        json = newJson;
    }

    public boolean isAtleastOneMatchFound() {
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

    public void setSearch(Author newAuthor) {
        search = newAuthor;
    }

    public Author getSearch() {
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

    public int getMinYear() {
        return minYear;
    }

    public void setMinYear(int minYear) {
        this.minYear = minYear;
    }

    public int getMaxYear() {
        return maxYear;
    }

    public void setMaxYear(int maxYear) {
        this.maxYear = maxYear;
    }

    public int getTempBirthYear() {
        return tempBirthYear;
    }

    public void setTempBirthYear(int newTempBirthYear) {
        tempBirthYear = newTempBirthYear;
    }

    public String wereThereCoAuthorsFound() {
        if (!coAuthorsFound) {
            return "display:none; ";
        } else {
            return "";
        }
    }

    public boolean isNYTfound() {
        return NYTfound;
    }

    public void setNYTfound(boolean NYTfound) {
        this.NYTfound = NYTfound;
    }

    public Set<Document> getSetDocs() {
        return setDocs;
    }

    public void setSetDocs(Set<Document> setDocs) {
        this.setDocs = setDocs;
    }

    public void addToSetDocs(Set<Document> newSetDocs) {
        this.setDocs.addAll(newSetDocs);
    }

    public void addToSetDocs(Document doc) {
        this.setDocs.add(doc);
    }

    public Set<Author> getSetAuthors() {
        return setAuthors;
    }

    public void setSetAuthors(Set<Author> setAuthors) {
        this.setAuthors = setAuthors;
    }

    public Set<CloseMatchBean> getSetCloseMatches() {
        return setCloseMatches;
    }

    public void setSetCloseMatches(TreeSet<CloseMatchBean> setCloseMatches) {
        this.setCloseMatches = setCloseMatches;
    }

    public void addToSetCloseMatches(TreeSet<CloseMatchBean> setCloseMatches) {
        this.setCloseMatches.addAll(setCloseMatches);
    }

    public void addToSetCloseMatches(CloseMatchBean closeMatch) {
        this.setCloseMatches.add(closeMatch);
    }

    public TreeSet<MapLabels> getSetMapLabels() {
        return setMapLabels;
    }

    public void setSetMapLabels(TreeSet<MapLabels> setMapLabels) {
        this.setMapLabels = setMapLabels;
    }

    public void addToSetMapLabels(TreeSet<MapLabels> setMapLabels) {
        this.setMapLabels.addAll(setMapLabels);
    }

    public boolean addToSetMapLabels(MapLabels mapLabel) {
        return this.setMapLabels.add(mapLabel);
    }

    public void removeFromSetMapLabels(MapLabels mapLabel) {
        this.setMapLabels.remove(mapLabel);
    }

    public List<Callable<PairSimple<Set<Document>, Author>>> getCalls() {
        return calls;
    }

    public String getFeedback() {
        return feedback;
    }

    public Set<Document> getSetMediaDocs() {
        return setMediaDocs;
    }

    public void setSetMediaDocs(Set<Document> setMediaDocs) {
        this.setMediaDocs = setMediaDocs;
    }

    public void addToSetMediaDocs(Set<Document> setMediaDocs) {
        this.setMediaDocs.addAll(setMediaDocs);
    }

    public void addToSetMediaDocs(Document mediaDoc) {
        this.setMediaDocs.add(mediaDoc);
    }

    public String skipPairs() {
        Iterator<CloseMatchBean> setCloseMatchesIterator = setCloseMatches.iterator();
        while (setCloseMatchesIterator.hasNext()) {
            CloseMatchBean closeMatchBean = setCloseMatchesIterator.next();
            addToSetMapLabels(new MapLabels(closeMatchBean.getAuthor1(), closeMatchBean.getAuthor3()));
            addToSetMapLabels(new MapLabels(closeMatchBean.getAuthor2(), closeMatchBean.getAuthor3()));
        }
        computationsBeforeReport();
        return "report?faces-redirect=true";
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

    public void persistAcademic() {
        PersistingAcademic pa = new PersistingAcademic();
        pa.setFullNameWithComma(search.getFullnameWithComma());
        pa.setBirthYear(search.getBirthYear());
        pa.setCountCoAuthors(setAuthors.size());
        pa.setCountDocuments(setDocs.size());
        pa.setSetAffiliations(search.getSetAffiliations());

        updateQueryPA = ds.createQuery(PersistingAcademic.class).field("fullNameWithComma").equal(search.getFullnameWithComma());
        opsPA = ds.createUpdateOperations(PersistingAcademic.class).inc("searchCount", 1);

        ds.update(updateQueryPA, opsPA, true);
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
        StringBuilder toBePersisted = new StringBuilder();
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String userAgent = externalContext.getRequestHeaderMap().get("User-Agent");
        if (search != null) {
            toBePersisted.append("\nSEARCH:");
            toBePersisted.append("\n");
            toBePersisted.append(search.getFullnameWithComma());
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
        ds.save(pf);
        System.out.println("feedback persisted: " + toBePersisted.toString());

    }

    public synchronized void pushCounter() {
//        int count = ds.find(GlobalEditsCounter.class).get().getGlobalCounter();
//        System.out.println("counter in pushCounter method in ControllerBean is:" + count);
//        PushContext pushContext = PushContextFactory.getDefault().getPushContext();
//        pushContext.push("/counter", String.valueOf(count).trim());
//        System.out.println("string value of count: ");
//        System.out.println("string value of count: " + String.valueOf(count).trim());
        count = ds.find(GlobalEditsCounter.class).get().getGlobalCounter();
    }

    class yearPublicationComparator implements Comparator<Document> {

        public int compare(Document d1, Document d2) {
            return d1.getYear().compareTo(d2.getYear());
        }
    }
}
