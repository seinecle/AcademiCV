package BL.NameDisambiguation;

import Controller.AdminPanel;
import Controller.ControllerBean;
import Model.Author;
import Model.CloseMatchBean;
import Model.MapLabels;
import Model.PersistingEdit;
import Utils.DiffSpelling;
import Utils.FindAllPairs;
import Utils.Pair;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;

public class SpellingDifferencesChecker {

    TreeSet<Author> setCloseMatches = new TreeSet();
    TreeSet<Author> setAuthorsWithEdits = new TreeSet();
    ArrayList authorsInOneDoc;
    Author currAuth;
    String mainFirstName;
    String mainLastName;
    boolean debug;
    private TreeMap<String, String> mapLabels = new TreeMap();
    private boolean atLeastOneMatchFound = false;
    static private String[] arrayTermsInFullnameInAuthor1;
    static private String[] arrayTermsInFullnameInAuthor2;
    static private HashSet<String> setTermsInFullnameAuthor1;
    static private HashSet<String> setTermsInFullnameAuthor2;
    static private String[] arrayTermsInSurnameInAuthor1;
    static private String[] arrayTermsInSurnameInAuthor2;
    static private HashSet<String> setTermsInSurnameAuthor1;
    static private HashSet<String> setTermsInSurnameAuthor2;
    static private HashSet<String> intersectFullNamesAuthor1And2;
    static private HashMap<String, Pair<String, Integer>> mapEdits;
    static private boolean wisdomCrowd;
    private Set<Author> setAuthorsOriginal;

    public SpellingDifferencesChecker(Set<Author> setAuthorsOriginal, boolean wisdomCrowd) {

        this.mainFirstName = ControllerBean.getSearch().getForename().trim();
        this.mainLastName = ControllerBean.getSearch().getSurname().trim();
        SpellingDifferencesChecker.wisdomCrowd = wisdomCrowd;
        this.setAuthorsOriginal = setAuthorsOriginal;

    }

    public boolean doAll() {

        debug = AdminPanel.wisdomCrowdsDebugStateTrueOrFalse();

        //if the user has selected the wisdom of the crowds in the UI
        //retrieve the persisted edits corresponding to the author currently being searched

        if (wisdomCrowd) {
            List<PersistingEdit> listEdits = ControllerBean.ds.find(PersistingEdit.class).field("reference").equal(ControllerBean.getSearch().getFullnameWithComma()).field("counter").greaterThan(1).asList();
            mapEdits = new HashMap();
            int elementCounter;
            String elementEditedForm;
            String elementOriginalForm;
            int elementCounterInMap;

            for (PersistingEdit element : listEdits) {
                elementCounter = element.getCounter();
//            System.out.println("elementCounter: " + elementCounter);
                elementEditedForm = element.getEditedForm();
//            System.out.println("elementEditedForm: " + elementEditedForm);
                elementOriginalForm = element.getOriginalForm();
//            System.out.println("elementOriginalForm: " + elementOriginalForm);

                if (!mapEdits.containsKey(elementOriginalForm)) {
                    mapEdits.put(elementOriginalForm, new Pair(elementEditedForm, elementCounter));
                } else {
                    elementCounterInMap = mapEdits.get(elementOriginalForm).getRight();
                    if (elementCounterInMap < elementCounter) {
                        mapEdits.put(elementOriginalForm, new Pair(elementEditedForm, elementCounter));
                    }
                }
            }
        }

        Iterator<Author> setAuthorsOriginalIterator = setAuthorsOriginal.iterator();
//        String currAuthFirstName;
//        String currAuthLastName;

        while (setAuthorsOriginalIterator.hasNext()) {
            currAuth = setAuthorsOriginalIterator.next();
//            currAuthFirstName = currAuth.getForename();
//            currAuthLastName = currAuth.getSurname();
//            if (currAuth.getForename().startsWith("Katy")) {
//                System.out.println("Katy found: " + currAuth.getFullnameWithComma());
//            }

//            currAuth.setUuid(ControllerBean.uuid.toString());
//            currAuth.setForename(currAuthFirstName);
//            currAuth.setSurname(currAuthLastName);

//                    System.out.println("currAuth.fullnameWithComma: " + currAuth.getFullnameWithComma());
//                    System.out.println("boolean exists: " + exists);


            if (wisdomCrowd && !debug) {
                boolean editAlreadyExists = mapEdits.containsKey(currAuth.getFullnameWithComma());
                if (editAlreadyExists) {
                    String editedForm = mapEdits.get(currAuth.getFullnameWithComma()).getLeft();
                    String[] terms = editedForm.split(",");
                    currAuth.setForename(terms[0].trim());
                    currAuth.setSurname(terms[1].trim());
                    currAuth.setFullname(terms[0].trim() + " " + terms[1].trim());
                }
            }
//            currAuth.setUuid(ControllerBean.uuid.toString());

            setAuthorsWithEdits.add(currAuth);
//            ControllerBean.ds.save(currAuth);
//                    System.out.println("author added: " + currAuth.getFullname());
        }

        Set<Pair<Author, Author>> setPairs = new FindAllPairs().getAllPairs(setAuthorsWithEdits);

//        if (setAuthors.contains(new Author("Katy","Borner"))){
//            System.out.println("set contains Katy Borner");
//        }
//        if (setAuthors.contains(new Author("Katy","Boerner"))){
//            System.out.println("set contains Katy Boerner");
//        }
        // ----------------------------
        System.out.println(
                "Number of co-authors found (with edits): " + setAuthorsWithEdits.size());
        System.out.println(
                "Number of distinct pairs: " + setPairs.size());
        System.out.println();
        // ----------------------------
        Iterator<Pair<Author, Author>> setPairsIterator = setPairs.iterator();
        Author author1;
        Author author2;
        Author author3;
        LinkedList<DiffSpelling.Diff> diffs;
        CloseMatchBean cmb;
        Pair<Author, Author> currPair;

        while (setPairsIterator.hasNext()) {
            currPair = setPairsIterator.next();
            author1 = currPair.getLeft();
            author2 = currPair.getRight();

            if ("Katy".equals(author1.getForename()) & "Katy".equals(author2.getForename())) {
//                System.out.println("author1: " + author1.getFullname());
//                System.out.println("author2: " + author2.getFullname());
//                System.out.println(computeWeightedLD(StringUtils.getLevenshteinDistance(author1.getSurname(), author2.getSurname()), author1.getSurname(), author2.getSurname()) > 0.3);
            }

            int levenDistance = StringUtils.getLevenshteinDistance(author1.getFullnameWithComma(), author2.getFullnameWithComma());

            float weightedLd = computeWeightedLD(levenDistance, author1.getFullnameWithComma(), author2.getFullnameWithComma());

            int match = thresholdLD(author1, author2, levenDistance, weightedLd);


            if (match != -1) {

                atLeastOneMatchFound = true;
                // ----------------------------
//                System.out.println(author1.toString() + " compared to " + author2.toString());
//                System.out.println("Levenshtein distance is: " + ld);
//                System.out.println("Weighted Levenshtein distance is: " + weightedLd);
//                System.out.println();
//                // ----------------------------



                author3 = getSuggestion(author1, author2, match);

                cmb = new CloseMatchBean();
                cmb.setUuid(ControllerBean.uuid);
                cmb.setAuthor1(author1.getFullnameWithComma());
                cmb.setAuthor2(author2.getFullnameWithComma());
                cmb.setAuthor3(author3.getFullnameWithComma());
                diffs = new DiffSpelling().diff_main(author1.getFullnameWithComma(), author2.getFullnameWithComma());
                cmb.setAuthor1Displayed(new DiffSpelling().diff_text1Custom(diffs));
                cmb.setAuthor2Displayed(new DiffSpelling().diff_text2Custom(diffs));
//                System.out.println("displayed 1" + cmb.getAuthor1Displayed());
//                System.out.println("displayed 2" + cmb.getAuthor2Displayed());
                setCloseMatches.add(new Author(cmb.getAuthor1()));
                setCloseMatches.add(new Author(cmb.getAuthor2()));

                //PERSIST CLOSE MATCHES
                ControllerBean.ds.save(cmb);

//                if (!author3.getFullname().trim().equals(mainFirstName + " " + mainLastName)) {
//                    System.out.println("ambiguous name detected: " + author1.getFullname());
//                    System.out.println("pairing with: " + author2.getFullname());
//                    System.out.println("proposal for merger: " + author3.getFullname());
//                    System.out.println("both ambiguous names added to mapLabels");
//                }
            }
        }
        Iterator<Author> setAuthorsIterator = setAuthorsWithEdits.iterator();
        Author currAuthor;

        while (setAuthorsIterator.hasNext()) {
            currAuthor = setAuthorsIterator.next();
            if (!setCloseMatches.contains(currAuthor)) {
                if (!currAuthor.getFullnameWithComma().trim().equals(mainFirstName + ", " + mainLastName)) {
//                    System.out.println("unambiguous author: " + currAuthor.getFullname());
//                    System.out.println("main Auth: " + mainFirstName + " " + mainLastName);

                    mapLabels.put(currAuthor.getFullnameWithComma(), currAuthor.getFullnameWithComma());
                }
            }
        }

//        System.out.println(
//                "size of MapLabels (should be total nb of authors minus main author): " + mapLabels.size());


        //PERSIST THE MAP OF LABELS
        Iterator<Entry<String, String>> mapLabelsIterator = mapLabels.entrySet().iterator();
        Entry<String, String> currMapEntry;

        while (mapLabelsIterator.hasNext()) {
            currMapEntry = mapLabelsIterator.next();
//            System.out.println("persisted in mapLabels: " + currMapEntry.getKey() + ", " + currMapEntry.getValue());
            ControllerBean.ds.save(new MapLabels(currMapEntry.getKey(), currMapEntry.getValue(), ControllerBean.uuid.toString()));
        }

        ControllerBean.setAuthors = setAuthorsWithEdits;

        return atLeastOneMatchFound;
    }

    private static float computeWeightedLD(Integer ld, String one, String two) {

        return (float) ld / Math.min(one.length(), two.length());
    }

    private static int thresholdLD(Author author1, Author author2, Integer ld, Float weightedLd) {


        arrayTermsInFullnameInAuthor1 = author1.getFullname().split(" ");
        arrayTermsInFullnameInAuthor2 = author2.getFullname().split(" ");


        //detects matches such as "Perreau, Adrian" & "Pinninck, Adrian Perreau De" 	
        if (arrayTermsInFullnameInAuthor1.length != arrayTermsInFullnameInAuthor2.length) {
            int element1;
            int element2;
            intersectFullNamesAuthor1And2 = new HashSet();
            setTermsInSurnameAuthor1 = new HashSet();
            setTermsInSurnameAuthor2 = new HashSet();
            setTermsInFullnameAuthor1 = new HashSet();
            setTermsInFullnameAuthor2 = new HashSet();
//            System.out.println("author1.getFullname() " + author1.getFullname());
//            System.out.println("author2.getFullname() " + author2.getFullname());

            setTermsInFullnameAuthor1.addAll(Arrays.asList(arrayTermsInFullnameInAuthor1));
            setTermsInFullnameAuthor2.addAll(Arrays.asList(arrayTermsInFullnameInAuthor2));
            Sets.intersection(setTermsInFullnameAuthor1, setTermsInFullnameAuthor2).copyInto(intersectFullNamesAuthor1And2);
            int intersectFullNamesSize = intersectFullNamesAuthor1And2.size();
            if (intersectFullNamesSize > 1) {

                element1 = intersectFullNamesAuthor1And2.toArray()[0].toString().length();
                element2 = intersectFullNamesAuthor1And2.toArray()[1].toString().length();

                arrayTermsInSurnameInAuthor1 = author1.getSurname().split(" ");
                arrayTermsInSurnameInAuthor2 = author2.getSurname().split(" ");

                setTermsInSurnameAuthor1.addAll(Arrays.asList(arrayTermsInSurnameInAuthor1));
                setTermsInSurnameAuthor2.addAll(Arrays.asList(arrayTermsInSurnameInAuthor2));
                int intersectSurnames = Sets.intersection(setTermsInSurnameAuthor1, setTermsInSurnameAuthor2).size();

                if ((intersectFullNamesSize > 1 & intersectSurnames > 0) | (intersectFullNamesSize > 1 && element1 > 1 && element2 > 1)) {
                    return 1;
                }
            }

        }

        if (author1.getForename().length() == 1 & author2.getForename().length() > 2) {
            if ((author1.getForename().equals(author2.getForename().subSequence(0, 1))
                    & author1.getSurname().equals(author2.getSurname()))) {
                return 0;
            }
        }
        if (author2.getForename().length() == 1 & author1.getForename().length() > 2) {
            if ((author2.getForename().equals(author1.getForename().subSequence(0, 1))
                    & author1.getSurname().equals(author2.getSurname()))) {
                return 0;
            }

        }
        if (weightedLd > 0.45) {
            return -1;
        } else if ((computeWeightedLD(StringUtils.getLevenshteinDistance(author1.getForename(), author2.getForename()), author1.getForename(), author2.getForename()) < 0.3)
                & (computeWeightedLD(StringUtils.getLevenshteinDistance(author1.getSurname(), author2.getSurname()), author1.getSurname(), author2.getSurname()) > 0.35)) {
            return -1;
        } else {
            return 0;
        }

    }

    private Author getSuggestion(Author author1, Author author2, int typeMatch) {
        Author author3;
        int code = typeMatch;
        boolean suggestionMade = false;
        String author1Forename = author1.getForename();
        String author2Forename = author2.getForename();
        String author1Surname = author1.getSurname();
        String author2Surname = author2.getSurname();

        String suggestedForename = author1Forename;
        String suggestedSurname = author1Surname;


        String[] author1ForenameArray = author1Forename.split(" ");
        String[] author2ForenameArray = author2Forename.split(" ");
        String[] suggestedForenameArray;


        //makes a suggestion for matches such as "Perreau, Adrian" & "Pinninck, Adrian Perreau De" 	
        if (code == 1) {
            if (author1.getFullname().length() > author2.getFullname().length()) {
                author3 = author1;
            } else {
                author3 = author2;

            }
            return author3;
        }

        //first names which have different number of names: take the longer first name
        if (author1ForenameArray.length > author2ForenameArray.length) {
            suggestedForename = author1Forename;
            suggestedSurname = author1Surname;
            suggestionMade = true;
        } else if (author1ForenameArray.length < author2ForenameArray.length) {
            suggestedForename = author2Forename;
            suggestedSurname = author1Surname;
            suggestionMade = true;
        }

        // first names with FIRST initial instead of full first names: get the full name
        suggestedForenameArray = suggestedForename.split(" ");
        if (author1ForenameArray[0].length() == 1 & author2ForenameArray[0].length() > 1) {
            suggestedForenameArray[0] = author2ForenameArray[0];
            suggestedForename = "";
            for (String element : suggestedForenameArray) {
                suggestedForename = suggestedForename + element + " ";
            }
            suggestedForename = suggestedForename.trim();
            suggestionMade = true;
        } else if (author2ForenameArray[0].length() == 1 & author1ForenameArray[0].length() > 1) {
            suggestedForenameArray[0] = author1ForenameArray[0];
            suggestedForename = "";
            for (String element : suggestedForenameArray) {
                suggestedForename = suggestedForename + element + " ";
            }
            suggestionMade = true;
        }


        // first names with SECOND initial instead of full first names: get the full name
        suggestedForenameArray = suggestedForename.split(" ");
        if ((author1ForenameArray.length > 1 && author2ForenameArray.length > 1)) {
            if (author1ForenameArray[1].length() == 1 & author2ForenameArray[1].length() > 1) {
                suggestedForenameArray[1] = author2ForenameArray[1];
                suggestedForename = "";
                for (String element : suggestedForenameArray) {
                    suggestedForename = suggestedForename + element + " ";
                }
                suggestedForename = suggestedForename.trim();
                suggestionMade = true;
            } else if (author2ForenameArray[1].length() == 1 & author1ForenameArray[1].length() > 1) {
                suggestedForenameArray[1] = author1ForenameArray[1];
                suggestedForename = "";
                for (String element : suggestedForenameArray) {
                    suggestedForename = suggestedForename + element + " ";
                }
                suggestionMade = true;
            }
        }



        // replacing "oe" by "o", "ue" by "u", "ae" by "a" (suspected cases of mispellings due to umlauts)
        if ((author1Forename.contains("oe") && !author2Forename.contains("oe")) || (author2Forename.contains("oe") && !author1Forename.contains("oe"))) {
            suggestedForename = suggestedForename.replaceAll("oe", "ö");
            suggestionMade = true;
        }
        if ((author1Surname.contains("oe") && !author2Surname.contains("oe")) || (author2Surname.contains("oe") && !author1Surname.contains("oe"))) {
            suggestedSurname = suggestedSurname.replaceAll("oe", "ö");
            suggestionMade = true;
        }
        if ((author1Forename.contains("ue") && !author2Forename.contains("ue")) || (author2Forename.contains("ue") && !author1Forename.contains("ue"))) {
            suggestedForename = suggestedForename.replaceAll("ue", "ü");
            suggestionMade = true;
        }
        if ((author1Surname.contains("ue") && !author2Surname.contains("ue")) || (author2Surname.contains("ue") && !author1Surname.contains("ue"))) {
            suggestedSurname = suggestedSurname.replaceAll("ue", "ü");
            suggestionMade = true;
        }
//        if ((author1Forename.contains("ae") && !author2Forename.contains("ae")) || (author2Forename.contains("ae") && !author1Forename.contains("ae"))) {
//            suggestedForename = suggestedForename.replaceAll("ae", "a");
//            suggestionMade = true;
//        }
//        if ((author1Surname.contains("ae") && !author2Surname.contains("ae")) || (author2Surname.contains("ae") && !author1Surname.contains("ae"))) {
//            suggestedSurname = suggestedSurname.replaceAll("ae", "u");
//            suggestionMade = true;
//        }

        if (suggestionMade) {
            author3 = new Author(suggestedForename, suggestedSurname);
            return author3;
        } else {

            //IF WE HAVE NO SPECIFIC SUGGESTION TO MAKE, WE SUGGEST THE LONGER NAME
            if (author1.toString().length() > author2.toString().length()) {
                author3 = author1;
            } else {
                author3 = author2;
            }
        }
        return author3;

    }
}
