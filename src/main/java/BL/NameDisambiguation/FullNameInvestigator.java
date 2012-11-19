package BL.NameDisambiguation;

import BL.DocumentHandling.AuthorMerger;
import Model.Author;
import Utils.WeightedLevenstheinDistanceCalculator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author C. Levallois
 *
 * This class tries to find first names and last names in a string that is just
 * a full name.
 *
 * It takes a set of fullnames as input, some of them being already identified
 * as first + last names
 *
 */
public class FullNameInvestigator {

    private boolean similarityAssessed;

    public FullNameInvestigator() {
    }

    public Set<Author> investigate(Set<Author> originalSet) {

        Set<Author> setAuthorsWithJustAFullname = new HashSet();
        Set<Author> setAuthorsWithFirstAndLastName = new HashSet();
        Set<Author> setAuthors = new TreeSet();


        Author currAuthor;
        Iterator<Author> originalSetIterator = originalSet.iterator();

        //separate authors with just fullnames and those with first and last names in 2 different sets.
        while (originalSetIterator.hasNext()) {
            currAuthor = originalSetIterator.next();
            if (currAuthor.getForename() == null && currAuthor.getSurname() == null) {
                setAuthorsWithJustAFullname.add(currAuthor);
//                System.out.println("author with just full name added: \"" + currAuthor.getFullname() + "\"");
            } else {
                setAuthorsWithFirstAndLastName.add(currAuthor);
//                System.out.println("author with first and last name added: " + currAuthor.getFullnameWithComma());

            }
        }

        System.out.println("size of set with just fullnames: " + setAuthorsWithJustAFullname.size());
        System.out.println("size of set with just first and last names: " + setAuthorsWithFirstAndLastName.size());

        Iterator<Author> setAuthorsWithJustAFullnameIterator = setAuthorsWithJustAFullname.iterator();
        Iterator<Author> setAuthorsWithFirstAndLastNameIterator;
        Author currAuthorWithJustFullName;
        Author currAuthorWithFirstnandLastName;
        Author authorWithFirstnandLastNameMostSimilar = null;
        String currFullName;
        String newLastName;
        String newFirstName;
        String[] allTerms;
        Author mergedAuthor;

        while (setAuthorsWithJustAFullnameIterator.hasNext()) {
            newLastName = "";
            newFirstName = "";
            similarityAssessed = false;

            currAuthorWithJustFullName = setAuthorsWithJustAFullnameIterator.next();
            setAuthorsWithFirstAndLastNameIterator = setAuthorsWithFirstAndLastName.iterator();
            currFullName = currAuthorWithJustFullName.getFullname();
//            System.out.println("currFullName is: " + currFullName);
            allTerms = currFullName.split(" ");
            float minDistance = 100;
            while (setAuthorsWithFirstAndLastNameIterator.hasNext()) {
                currAuthorWithFirstnandLastName = setAuthorsWithFirstAndLastNameIterator.next();
                float distance = WeightedLevenstheinDistanceCalculator.compute(currFullName, currAuthorWithFirstnandLastName.getFullname());

                // if an identical name is found, end the search and copy the first and last name
                // 1

                if (distance == 0) {

                    mergedAuthor = new AuthorMerger().mergeAuthors(currAuthorWithJustFullName, currAuthorWithFirstnandLastName);
                    mergedAuthor.setForename(currAuthorWithFirstnandLastName.getForename());
                    mergedAuthor.setSurname(currAuthorWithFirstnandLastName.getSurname());
//                    if (currAuthorWithJustFullName.getFullname().equals("Werner Ebeling")) {
//                        System.out.println("Werner Ebeling gets mentioned in distance == 0");
//                        System.out.println("perfect match found");
//                        System.out.println("author added, first name is: " + currAuthorWithFirstnandLastName.getForename() + ", last name is: " + currAuthorWithFirstnandLastName.getSurname());
//                        System.out.println("original fullname was: " + currFullName);
//                        System.out.println("-----------");
//                        System.out.println("mergedAuthor fullnamewith comma is: " + mergedAuthor.getFullnameWithComma());
//
//                    }

                    setAuthors.add(mergedAuthor);
//                    System.out.println(mergedAuthor.getFullnameWithComma() + " added in 1");

                    similarityAssessed = true;

                    break;
                }
                if (distance < minDistance) {
                    minDistance = distance;
                    authorWithFirstnandLastNameMostSimilar = currAuthorWithFirstnandLastName;
                }
            }

            if (similarityAssessed) {
                continue;
            }

            // if an identical name has not been found,
            // if an similar name has not been found,
            //just split the fullname at its last term ("Jose Luis Sanchez de Lucia" becomes "Jose Luis Sanchez de", "Lucia"
            //2
            if (authorWithFirstnandLastNameMostSimilar == null) {
//                if (currAuthorWithJustFullName.getFullname().equals("Werner Ebeling")) {
//                    System.out.println("Werner Ebeling gets mentioned in no identical name found");
//                }

                for (int i = 0; i < allTerms.length - 1; i++) {
                    newFirstName = newFirstName.concat(allTerms[i]).concat(" ");
                }
                newLastName = newLastName.concat(allTerms[allTerms.length - 1]);
                currAuthorWithJustFullName.setForename(newFirstName.trim());
                currAuthorWithJustFullName.setSurname(newLastName.trim());
                setAuthors.add(currAuthorWithJustFullName);
//                System.out.println(currAuthorWithJustFullName.getFullnameWithComma() + " added in 2");

                continue;
            }

            int nbTermsFullNameWithFirstAndLastName = authorWithFirstnandLastNameMostSimilar.getFullname().split(" ").length;
            int nbTermsFullNameWithJustFullName = allTerms.length;

            // if an identical name has not been found,
            // if a similar name has been found, with the same number of terms in the name,
            // take the same cut off btwn first name and last name and applies it.
            // 3

            if (minDistance < 0.10 && (nbTermsFullNameWithFirstAndLastName == nbTermsFullNameWithJustFullName)) {
//                if (currAuthorWithJustFullName.getFullname().equals("Werner Ebeling")) {
//                    System.out.println("Werner Ebeling gets mentioned in similar has been found");
//                }

                int nbTermsSurName = authorWithFirstnandLastNameMostSimilar.getSurname().split(" ").length;
                for (int i = 0; i < nbTermsSurName; i++) {
                    newLastName = newLastName.concat(allTerms[allTerms.length - 1 - i]).concat(" ");
                }
                for (int i = 0; i < (allTerms.length - nbTermsSurName); i++) {
                    newFirstName = newFirstName.concat(allTerms[i]).concat(" ");
                }
                currAuthorWithJustFullName.setForename(newFirstName.trim());
                currAuthorWithJustFullName.setSurname(newLastName.trim());
//                System.out.println(
//                        "no perfect match found but still very similar");
//                System.out.println("author added, first name is: " + newFirstName.trim() + ", last name is: " + newLastName.trim());
//                System.out.println("original fullname was: " + currFullName);
//                System.out.println("-----------");
                setAuthors.add(currAuthorWithJustFullName);
//                System.out.println(currAuthorWithJustFullName.getFullnameWithComma() + " added in 3");


                continue;
            }

            // add a condition here dealing with a high similarity but a different number of terms
            // that's probably the case of a missing midlle name

            //not a high similarity found. We deal with the case of particules.
            //4
            if (nbTermsFullNameWithJustFullName > 2 & currFullName.toLowerCase().matches(" van | von | de | ten | du | del ")) {
                boolean stillInFirstName = true;
                for (String element : allTerms) {

                    if (element.toLowerCase().trim().matches("van|von|de|du|del")) {
                        stillInFirstName = false;
                    }
                    if (stillInFirstName) {
                        newFirstName = newFirstName.concat(element).concat(" ");
                    } else {
                        newLastName = newLastName.concat(element).concat(" ");
                    }
                }

                currAuthorWithJustFullName.setForename(newFirstName.trim());
                currAuthorWithJustFullName.setSurname(newLastName.trim());
                setAuthors.add(currAuthorWithJustFullName);
//                System.out.println(currAuthorWithJustFullName.getFullnameWithComma() + " added in 4");

//                if (currAuthorWithJustFullName.getFullname().equals("Werner Ebeling")) {
//                    System.out.println("author added, first name is: " + newFirstName.trim() + ", last name is: " + newLastName.trim());
//                    System.out.println("original fullname was: " + currFullName);
//                    System.out.println("-----------");
//                }
                continue;

            }

            //last case: there is no strong similarity or whatever
            // just take the last term as the last name, previous terms will be first name
            //5
            for (int i = 0; i < allTerms.length - 1; i++) {
//                System.out.println("allTerms in loop: " + allTerms[i]);
                newFirstName = newFirstName.concat(allTerms[i]).concat(" ");
            }
            newLastName = newLastName.concat(allTerms[allTerms.length - 1]);
            currAuthorWithJustFullName.setForename(newFirstName.trim());
            currAuthorWithJustFullName.setSurname(newLastName.trim());
//            if (currAuthorWithJustFullName.getFullname().equals("Werner Ebeling")) {
//
//                System.out.println("no similarity found, simple heuristic applied");
//                System.out.println("author added, first name is: " + newFirstName.trim() + ", last name is: " + newLastName.trim());
//                System.out.println("original fullname was: " + currFullName);
//                System.out.println("-----------");
//            }
            setAuthors.add(currAuthorWithJustFullName);
//            System.out.println(currAuthorWithJustFullName.getFullnameWithComma() + " added in 5");



        }// end loop through all authors which just have a full name

        setAuthors.addAll(setAuthorsWithFirstAndLastName);
        return setAuthors;

    }
}
