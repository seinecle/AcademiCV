/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.DocumentHandling;

import Model.Affiliation;
import Model.Author;
import java.util.HashSet;

/**
 *
 * @author C. Levallois
 */
public class AuthorMerger {

    public HashSet<Author> mergeAuthorSets(HashSet<Author> authorSet1, HashSet<Author> authorSet2) {
        HashSet<Author> setMergedAuthors = new HashSet();
        Author mergedAuthor;

        for (Author eSet1 : authorSet1) {
            for (Author eSet2 : authorSet2) {
//                System.out.println("eSet1is:");
//                System.out.println(eSet1.getFullname());
//                System.out.println("eSet2is:");
//                System.out.println(eSet2.getFullname());
                if (eSet1.getFullname().equals(eSet2.getFullname())) {
                    mergedAuthor = mergeAuthors(eSet1, eSet2);
                    setMergedAuthors.add(mergedAuthor);

                }
            }
        }
        return setMergedAuthors;
    }

    public Author mergeAuthors(Author author1, Author author2) {
        Author currMergedAuthor;
        HashSet<Affiliation> setAffiliations = new HashSet();
//        System.out.println("mergeAuthors, author1 is:");
//        System.out.println(author1.getFullname());
//        System.out.println(author1.getMostRecentAffiliation());
//        System.out.println("mergeAuthors, author2 is:");
//        System.out.println(author1.getFullname());
//        System.out.println(author1.getMostRecentAffiliation());


        // start by taking as a basis for mergedAuthor the author that has a distinction between first and last name
        if (author1.getForename() == null) {
            currMergedAuthor = author2;
        } else {
            currMergedAuthor = author1;
        }

        //merge affiliations
        setAffiliations.addAll(author1.getSetAffiliations());
        setAffiliations.addAll(author2.getSetAffiliations());
        currMergedAuthor.setSetAffiliations(setAffiliations);
//        if (setAffiliations.iterator().hasNext()) {
//            System.out.println("printing affiliations in the author merge method");
//            System.out.println(setAffiliations.iterator().next().toString());
//        }

        return currMergedAuthor;


    }
}
