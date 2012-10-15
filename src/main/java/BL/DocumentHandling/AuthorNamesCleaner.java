/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.DocumentHandling;

import Controller.ControllerBean;
import Model.Author;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author C. Levallois
 */
public class AuthorNamesCleaner {

    public static HashSet<Author> clean(Set<Author> setAuthors) {
        HashSet<Author> returnedSet = new HashSet();

        Iterator<Author> setAuthorsIterator = setAuthors.iterator();
        Author currAuthor;
        while (setAuthorsIterator.hasNext()) {
            currAuthor = setAuthorsIterator.next();
            currAuthor.setForename(currAuthor.getForename().replace(".", ""));
            currAuthor.setSurname(currAuthor.getSurname().replace(".", ""));
            currAuthor.setFullname(currAuthor.getFullname().replace(".", ""));
            if (currAuthor.getForename().startsWith("By ")) {
                currAuthor.setForename(currAuthor.getForename().substring(3));
            }
            
            //we don't keep in the set of authors the badly spelled versions of the author's name
            
            if (StringUtils.stripAccents(currAuthor.getFullname()).toLowerCase().replaceAll("-", " ").trim().equals(StringUtils.stripAccents(ControllerBean.getSearch().getFullname().toLowerCase().replaceAll("-", " ").trim()))) {
                continue;
            }


            returnedSet.add(currAuthor);
        }

        return returnedSet;

    }
}
