/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.DocumentHandling;

import Controller.ControllerBean;
import Model.Author;
import Model.Document;
import com.google.common.collect.HashMultiset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author C. Levallois
 */
public class AuthorsExtractor {

    public AuthorsExtractor() {
    }

    public static HashSet<Author> extractFromOneDoc(Document doc) {
        return doc.getAuthors();
    }

    public static HashMultiset<Author> extractFromSetDocs(Set<Document> setDocs) {

        HashMultiset<Author> setAllAuthors = HashMultiset.create();
        Iterator<Document> setDocsIterator = setDocs.iterator();
        while (setDocsIterator.hasNext()) {
            setAllAuthors.addAll(setDocsIterator.next().getAuthors());
        }
        return setAllAuthors;
    }

    public static void extractCurrSearchedAuthor() {
        Iterator<Author> setAuthorsIterator = ControllerBean.setAuthors.iterator();
        Author currAuthor;
        Author mergedAuthor;
        while (setAuthorsIterator.hasNext()) {
            currAuthor = setAuthorsIterator.next();
            if (currAuthor.getFullname().equals(ControllerBean.getSearch().getFullname())) {
                mergedAuthor = AuthorMerger.mergeAuthors(currAuthor, ControllerBean.getSearch());
                ControllerBean.setSearch(mergedAuthor);
            }
        }
    }
}
