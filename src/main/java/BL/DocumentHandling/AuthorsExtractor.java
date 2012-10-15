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
import java.util.List;
import java.util.Set;
import java.util.UUID;

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

    public static HashMultiset<Author> extractFromDocsInDB(UUID uuid) {
        HashMultiset<Author> setAllAuthors = HashMultiset.create();
        List<Document> listDocuments = ControllerBean.ds.find(Document.class).field("uuid").equal(ControllerBean.uuid.toString()).retrievedFields(true, "authors").asList();
        Iterator<Document> listDocumentsIterator = listDocuments.iterator();
        while (listDocumentsIterator.hasNext()) {
            setAllAuthors.addAll(listDocumentsIterator.next().getAuthors());

        }
        return setAllAuthors;
    }

    public static HashMultiset<Author> extractFromSetDocs(Set<Document> setDocs) {
        HashMultiset<Author> setAllAuthors = HashMultiset.create();
        Iterator<Document> setDocsIterator = setDocs.iterator();
        while (setDocsIterator.hasNext()) {
            setAllAuthors.addAll(setDocsIterator.next().getAuthors());

        }
        return setAllAuthors;
    }
}
