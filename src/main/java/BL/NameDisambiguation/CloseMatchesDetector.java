/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.NameDisambiguation;

import Model.Author;
import Model.CloseMatchBean;
import Utils.DiffSpelling;
import Utils.FindAllPairs;
import Utils.Pair;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author C. Levallois
 */
public class CloseMatchesDetector {

    private String[] arrayTermsInFullnameInAuthor1;
    private String[] arrayTermsInFullnameInAuthor2;
    private HashSet<String> setTermsInFullnameAuthor1;
    private HashSet<String> setTermsInFullnameAuthor2;
    private String[] arrayTermsInSurnameInAuthor1;
    private String[] arrayTermsInSurnameInAuthor2;
    private HashSet<String> setTermsInSurnameAuthor1;
    private HashSet<String> setTermsInSurnameAuthor2;
    private HashSet<String> intersectFullNamesAuthor1And2;
    private HashMap<String, Pair<String, Integer>> mapEdits;
    private Author search;
    private Set<CloseMatchBean> setCloseMatches = new HashSet();

    public Set<CloseMatchBean> check(Set<Author> setAuthorsWithEdits) {
        Set<Pair<Author, Author>> setPairs = new FindAllPairs().getAllPairs(setAuthorsWithEdits);
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
//                System.out.println(computeWeightedLD(StringUtils.getLevenshteinDistance(author1.getSurname(), author2.getSurname()), author1.getSurname(), author2.getSurname()) > 0.3);
//            }

            int levenDistance = StringUtils.getLevenshteinDistance(author1.getFullnameWithComma(), author2.getFullnameWithComma());

            float weightedLd = computeWeightedLD(levenDistance, author1.getFullnameWithComma(), author2.getFullnameWithComma());

            int match = thresholdLD(author1, author2, levenDistance, weightedLd);


            if (match != -1) {

                // ----------------------------

                author3 = getSuggestion(author1, author2, match);

                cmb = new CloseMatchBean();
                cmb.setAuthor1(author1.getFullnameWithComma());
                cmb.setAuthor2(author2.getFullnameWithComma());
                cmb.setAuthor3(author3.getFullnameWithComma());
                diffs = new DiffSpelling().diff_main(author1.getFullnameWithComma(), author2.getFullnameWithComma());
                cmb.setAuthor1Displayed(new DiffSpelling().diff_text1Custom(diffs));
                cmb.setAuthor2Displayed(new DiffSpelling().diff_text2Custom(diffs));
//                System.out.println("displayed 1" + cmb.getAuthor1Displayed());
//                System.out.println("displayed 2" + cmb.getAuthor2Displayed());
//                setCloseMatches.add(new Author(cmb.getAuthor1()));
//                setCloseMatches.add(new Author(cmb.getAuthor2()));
                  setCloseMatches.add(cmb);  

            }
        }
        return setCloseMatches;
    }

    private float computeWeightedLD(Integer ld, String one, String two) {
        return (float) ld / Math.min(one.length(), two.length());
    }

    private int thresholdLD(Author author1, Author author2, Integer ld, Float weightedLd) {
        arrayTermsInFullnameInAuthor1 = author1.getFullname().split(" ");
        arrayTermsInFullnameInAuthor2 = author2.getFullname().split(" ");

        //first, let's rule out an obvious case:
        //if both surnames have a single term, and the ld between these terms is above 2, then we don't have a match
        if ((author1.getSurname().split(" ").length) == 1 & (author2.getSurname().split(" ").length) == 1) {

            int distSurname = StringUtils.getLevenshteinDistance(author1.getSurname(), author2.getSurname());
            if (distSurname > 2) {
                return -1;
            }
        }

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