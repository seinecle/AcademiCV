/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.DocumentHandling;

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

    public HashSet<Author> extractFromOneDoc(Document doc) {
        return doc.getAuthors();
    }

    public HashMultiset<Author> extractFromSetDocs(Set<Document> setDocs) {

        HashMultiset<Author> setAllAuthors = HashMultiset.create();
        Iterator<Document> setDocsIterator = setDocs.iterator();
        while (setDocsIterator.hasNext()) {
            setAllAuthors.addAll(setDocsIterator.next().getAuthors());
        }
        return setAllAuthors;
    }

    public Author extractCurrSearchedAuthor(Set<Author> setAuthors, Author search) {
        Iterator<Author> setAuthorsIterator = setAuthors.iterator();
        Author searchAuthor = search;
        Author currAuthor;
        Author mergedAuthor;
        while (setAuthorsIterator.hasNext()) {
            currAuthor = setAuthorsIterator.next();
            if (currAuthor.getFullname().equals(search.getFullname())) {
                mergedAuthor = new AuthorMerger().mergeAuthors(currAuthor, search);
                searchAuthor = mergedAuthor;
            }
        }
        return searchAuthor;
    }
}
