/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.DocumentHandling;

import Model.Author;
import com.google.common.collect.HashMultiset;
import java.util.Iterator;

/**
 *
 * @author C. Levallois
 */
public class AuthorNamesCleaner {



    public HashMultiset<Author> cleanFullNameWithComma(HashMultiset<Author> setAuthors) {
        HashMultiset<Author> returnedSet = HashMultiset.create();

        Iterator<Author> setAuthorsIterator = setAuthors.iterator();
        Author currAuthor;
        while (setAuthorsIterator.hasNext()) {
            currAuthor = setAuthorsIterator.next();
            currAuthor.setFullnameWithComma(currAuthor.getFullnameWithComma().replace(".", ""));
            currAuthor.setFullnameWithComma(currAuthor.getFullnameWithComma().replace("By ", ""));



            returnedSet.add(currAuthor);
        }

        return returnedSet;

    }

    public HashMultiset<Author> cleanFullName(HashMultiset<Author> setAuthors) {
        HashMultiset<Author> returnedSet = HashMultiset.create();

        Iterator<Author> setAuthorsIterator = setAuthors.iterator();
        Author currAuthor;
        while (setAuthorsIterator.hasNext()) {
            currAuthor = setAuthorsIterator.next();
            if (currAuthor.getSetAffiliations().iterator().hasNext()) {
                System.out.println("printing affiliations in the author extractor:");
                System.out.println(currAuthor.getSetAffiliations().iterator().next());
            }
            currAuthor.setFullname(currAuthor.getFullname().replace(".", ""));
            currAuthor.setFullname(currAuthor.getFullname().replace("By ", ""));

            if (currAuthor.getForename() != null) {
                currAuthor.setForename(currAuthor.getForename().replace(".", ""));
                currAuthor.setForename(currAuthor.getForename().replace("By ", ""));
            }

            if (currAuthor.getSurname() != null) {
                currAuthor.setSurname(currAuthor.getSurname().replace(".", ""));
                currAuthor.setSurname(currAuthor.getSurname().replace("By ", ""));
            }

            //we don't keep in the set of authors the badly spelled versions of the author's name
//            if (StringUtils.stripAccents(currAuthor.getFullname()).toLowerCase().replaceAll("-", " ").trim().equals(StringUtils.stripAccents(ControllerBean.getSearch().getFullname().toLowerCase().replaceAll("-", " ").trim()))) {
//                continue;
//            }


            returnedSet.add(currAuthor);
        }

        return returnedSet;

    }
}
