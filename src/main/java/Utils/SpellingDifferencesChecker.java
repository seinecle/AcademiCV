package Utils;

import BL.APIs.Mendeley.MendeleyDoc;
import Controller.ControllerBean;
import Controller.AdminPanel;
import Model.Author;
import Model.CloseMatchBean;
import Model.MapLabels;
import Model.PersistingEdit;
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

    List<MendeleyDoc> listMendeleyDocs = Controller.ControllerBean.ds.find(MendeleyDoc.class).field("uuid").equal(ControllerBean.uuid.toString()).asList();
    TreeSet<Author> setCloseMatches = new TreeSet();
    TreeSet<Author> setAuthors = new TreeSet();
    Iterator<MendeleyDoc> listMendeleyDocsIterator = listMendeleyDocs.iterator();
    ArrayList authorsInOneDoc;
    Author currAuth;
    String mainFirstName;
    String mainLastName;
    boolean debug = AdminPanel.wisdomCrowdsDebugStateTrueOrFalse();
    private TreeMap<Author, Author> mapLabels = new TreeMap();
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

    public SpellingDifferencesChecker(String firstname, String lastname, boolean wisdomCrowd) {

        this.mainFirstName = firstname.trim();
        this.mainLastName = lastname.trim();
        SpellingDifferencesChecker.wisdomCrowd = wisdomCrowd;
    }

    public boolean doAll() {

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
        System.out.println(
                "Nb Docs retrieved from storage: " + listMendeleyDocs.size());
        MendeleyDoc currMendeleyDoc;

        while (listMendeleyDocsIterator.hasNext()) {
            currMendeleyDoc = listMendeleyDocsIterator.next();
            if (currMendeleyDoc.getAuthors() != null) {
                authorsInOneDoc = currMendeleyDoc.getAuthors();

                //SANITY CHECKS ON THE AUTHORS OF THE DOCS
                Iterator<Author> authorsInOneDocIterator = authorsInOneDoc.iterator();
                String currAuthFirstName;
                String currAuthLastName;
                while (authorsInOneDocIterator.hasNext()) {
                    currAuth = authorsInOneDocIterator.next();
                    currAuthFirstName = currAuth.getForename().trim();
                    if (currAuthFirstName.startsWith("By ")) {
                        currAuthFirstName = currAuthFirstName.substring(3);
                    }
                    currAuthLastName = currAuth.getSurname().trim();
//                    if (currAuthFirstName.startsWith("Adrian")) {
//                        System.out.println("firstName: " + currAuthFirstName);
//                        System.out.println("lastName: " + currAuthLastName);
//                    }

                    currAuth.setUuid(ControllerBean.uuid.toString());
                    currAuth.setForename(currAuthFirstName);
                    currAuth.setSurname(currAuthLastName);
                    if (StringUtils.stripAccents(currAuth.getFullnameWithComma()).equals(StringUtils.stripAccents(ControllerBean.getSearch().getFullnameWithComma()))) {
                        continue;
                    }

//                    System.out.println("currAuth.fullnameWithComma: " + currAuth.getFullnameWithComma());
//                    System.out.println("boolean exists: " + exists);
                    if (wisdomCrowd && !debug) {
                        boolean editAlreadyExists = mapEdits.containsKey(currAuth.getFullnameWithComma());
                        if (editAlreadyExists) {
                            String editedForm = mapEdits.get(currAuth.getFullnameWithComma()).getLeft();
                            String[] terms = editedForm.split(",");
                            currAuth = new Author(terms[1].trim(), terms[0].trim());
                        }
                    }
                    currAuth.setUuid(ControllerBean.uuid.toString());

                    setAuthors.add(currAuth);
                    ControllerBean.ds.save(currAuth);
//                    System.out.println("author added: " + currAuth.getFullname());
                }

            }
        }
        Set<Pair<Author, Author>> setPairs = getAllPairs(setAuthors);

//        if (setAuthors.contains(new Author("Katy","Borner"))){
//            System.out.println("set contains Katy Borner");
//        }
//        if (setAuthors.contains(new Author("Katy","Boerner"))){
//            System.out.println("set contains Katy Boerner");
//        }
        // ----------------------------
        System.out.println(
                "Number of co-authors found: " + setAuthors.size());
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

//            if ("Katy".equals(author1.getForename()) & "Katy".equals(author2.getForename())) {
//                System.out.println("author1: " + author1.getFullname());
//                System.out.println("author2: " + author2.getFullname());
//            }

            int ld = computeLevenshteinDistance(author1.getFullnameWithComma(), author2.getFullnameWithComma());
            float weightedLd = computeWeightedLD(ld, author1.getFullnameWithComma(), author2.getFullnameWithComma());

            int match = thresholdLD(author1, author2, ld, weightedLd);


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
        Iterator<Author> setAuthorsIterator = setAuthors.iterator();
        Author currAuthor;

        while (setAuthorsIterator.hasNext()) {
            currAuthor = setAuthorsIterator.next();
            if (!setCloseMatches.contains(currAuthor)) {
                if (!currAuthor.getFullnameWithComma().trim().equals(mainFirstName + ", " + mainLastName)) {
//                    System.out.println("unambiguous author: " + currAuthor.getFullname());
//                    System.out.println("main Auth: " + mainFirstName + " " + mainLastName);

                    mapLabels.put(currAuthor, currAuthor);
                }
            }
        }

        System.out.println(
                "size of MapLabels (should be total nb of authors minus main author): " + mapLabels.size());
//        if (RingSessionBean.mapCloseMatches.get(new Author("Almila", "Akdag Salah")) == new Author("Almila", "Akdag Salah")) {
//            System.out.println("Almila Akdag Salah is mapped to Almila Akdag Salah, not good!");
//        } else {
//            System.out.println("Almila Akdag Salah is NOT mapped to Almila Akdag Salah, ok!");
//        }


        //PERSIST THE MAP OF LABELS
        Iterator<Entry<Author, Author>> mapLabelsIterator = mapLabels.entrySet().iterator();
        Entry<Author, Author> currMapEntry;

        while (mapLabelsIterator.hasNext()) {
            currMapEntry = mapLabelsIterator.next();
//            System.out.println("persisted in mapLabels: " + currMapEntry.getKey() + ", " + currMapEntry.getValue());
            ControllerBean.ds.save(new MapLabels(currMapEntry.getKey(), currMapEntry.getValue(), ControllerBean.uuid.toString()));
        }
        //      return setCloseMatches;
        return atLeastOneMatchFound;
    }

    private Set<Pair<Author, Author>> getAllPairs(Set<Author> setAuthorsInHere) {
        Set<Author> setAuthorsProcessed = new TreeSet<Author>();
        Set<Pair<Author, Author>> setPairs = new TreeSet<Pair<Author, Author>>();
        Iterator<Author> setAuthorsIteratorA = setAuthorsInHere.iterator();
        Iterator<Author> setAuthorsIteratorB;
        Author currAuthorA;
        Author currAuthorB;
        while (setAuthorsIteratorA.hasNext()) {
            currAuthorA = setAuthorsIteratorA.next();
            setAuthorsIteratorB = setAuthorsInHere.iterator();
            while (setAuthorsIteratorB.hasNext()) {
                currAuthorB = setAuthorsIteratorB.next();
                if (!setAuthorsProcessed.contains(currAuthorB) && currAuthorA != currAuthorB) {
                    setPairs.add(new Pair(currAuthorA, currAuthorB));
                }
            }
            setAuthorsProcessed.add(currAuthorA);
        }
        System.out.println("size of setAuthor after the finding all pairs operation: " + this.setAuthors.size());
        return setPairs;

    }

    private static int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    private static int computeLevenshteinDistance(CharSequence str1,
            CharSequence str2) {
        int[][] distance = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++) {
            distance[i][0] = i;
        }
        for (int j = 0; j <= str2.length(); j++) {
            distance[0][j] = j;
        }

        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                distance[i][j] = minimum(
                        distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1,
                        distance[i - 1][j - 1]
                        + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0
                        : 1));
            }
        }

        return distance[str1.length()][str2.length()];
    }

    private static float computeWeightedLD(Integer ld, String one, String two) {

        return (float) ld / Math.min(one.length(), two.length());
    }

    private static int thresholdLD(Author author1, Author author2, Integer ld, Float weightedLd) {


//        arrayTermsInForenameInAuthor1 = author1.getForename().split(" ");
//        arrayTermsInForenameInAuthor2 = author2.getForename().split(" ");
//        setTermsInForenameAuthor1.addAll(Arrays.asList(arrayTermsInForenameInAuthor1));
//        setTermsInForenameAuthor2.addAll(Arrays.asList(arrayTermsInForenameInAuthor2));
//        setTermsInForenameAuthor1.removeAll(setTermsInForenameAuthor2);
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
//        System.out.println("author1.getForename(): " + author1.getForename());
//        System.out.println("author2.getForename(): " + author2.getForename());

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
        if (weightedLd > 0.4) {
            return -1;
        } else if ((computeWeightedLD(computeLevenshteinDistance(author1.getForename(), author2.getForename()), author1.getForename(), author2.getForename()) < 0.3)
                & (computeWeightedLD(computeLevenshteinDistance(author1.getSurname(), author2.getSurname()), author1.getSurname(), author2.getSurname()) > 0.3)) {
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
            return author3 = new Author(suggestedForename, suggestedSurname);
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
