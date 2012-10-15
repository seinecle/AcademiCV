/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.DocumentHandling;

import Model.Author;
import com.google.common.collect.HashMultiset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author C. Levallois
 */
public class AuthorStatsHandler {

    public static Set<Author> findTimesCited(HashMultiset<Author> setAuthors) {
        Set setToReturn = new HashSet();
        Iterator<Author> setAuthorsIterator = setAuthors.elementSet().iterator();
        int times = 0;
        Author currAuthor;
        while (setAuthorsIterator.hasNext()) {
            currAuthor = setAuthorsIterator.next();
            currAuthor.setTimesMentioned(setAuthors.count(currAuthor));
            setToReturn.add(currAuthor);
        }

        return setToReturn;

    }
}
