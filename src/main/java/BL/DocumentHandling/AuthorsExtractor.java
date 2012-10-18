/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.DocumentHandling;

import Controller.ControllerBean;
import Model.Author;
import Model.Document;
import Utils.Pair;
import com.google.common.collect.HashMultiset;
import java.util.HashMap;
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

    public static HashMultiset<Author> extractFromSetDocsWithDatesFixing(Set<Document> setDocs) {
        HashMultiset<Author> setAllAuthors = HashMultiset.create();
        HashMultiset<Author> bufferSetAllAuthors = HashMultiset.create();
        HashMap<String, Pair<Integer, Integer>> mapAuthorToDates = new HashMap();
        Iterator<Document> setDocsIterator = setDocs.iterator();
        Document currDoc;
        Set<Author> currDocSetAuthors;
        Iterator<Author> currDocSetAuthorsIterator;
        Iterator<Author> bufferSetAllAuthorsIterator;
        Author currAuthor;

        // this loop collects the earliest and latest date of publication for each author
        while (setDocsIterator.hasNext()) {
            currDoc = setDocsIterator.next();
            currDocSetAuthors = currDoc.getAuthors();
            currDocSetAuthorsIterator = currDocSetAuthors.iterator();
            while (currDocSetAuthorsIterator.hasNext()) {
                currAuthor = currDocSetAuthorsIterator.next();

                //deals with dates
                if (mapAuthorToDates.containsKey(currAuthor.getFullname())) {
                    int recordedStartDate = mapAuthorToDates.get(currAuthor.getFullname()).getLeft();
                    int recordedEndDate = mapAuthorToDates.get(currAuthor.getFullname()).getRight();
                    if (currDoc.getYear() < recordedStartDate) {
                        recordedStartDate = currDoc.getYear();
                    }
                    if (currDoc.getYear() > recordedEndDate) {
                        recordedStartDate = currDoc.getYear();
                    }
                    mapAuthorToDates.put(currAuthor.getFullname(), new Pair(recordedStartDate, recordedEndDate));
                } else {
                    mapAuthorToDates.put(currAuthor.getFullname(), new Pair(currDoc.getYear(), currDoc.getYear()));

                }

                // should do the same with affiliations (with a map of Authors, affiliation
                bufferSetAllAuthors.add(currAuthor);
            }

            bufferSetAllAuthorsIterator = bufferSetAllAuthors.iterator();


            while (bufferSetAllAuthorsIterator.hasNext()) {
                currAuthor = bufferSetAllAuthorsIterator.next();
                currAuthor.setYearFirstCollab(mapAuthorToDates.get(currAuthor.getFullname()).getLeft());
                currAuthor.setYearLastCollab(mapAuthorToDates.get(currAuthor.getFullname()).getRight());
                setAllAuthors.add(currAuthor);
            }


        }

        setAllAuthors.addAll(setDocsIterator.next().getAuthors());
        ControllerBean.mapAuthorToDates = mapAuthorToDates;

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
