/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.DocumentHandling;

import Controller.ControllerBean;
import Model.Author;
import Model.Document;
import Model.MapLabels;
import Utils.Pair;
import com.google.common.collect.HashMultiset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author C. Levallois
 */
public class AuthorStatsHandler {

    public static Set<Author> updateAuthorNamesAfterUserInput() {

        HashMap<Integer, Document> mapDocs;
        Iterator<Integer> mapDocsIterator;
        HashMap<String, Pair<Integer, Integer>> mapAuthorNameToDates;
        HashMap<String, String> mapIncorrectToCorrect;
        HashSet<Author> currSetAuthors;
        HashSet<Author> bufferSetAuthor;
        Iterator<Author> currSetAuthorsIterator;
        HashMultiset<Author> multisetAuthors;
        Iterator<Author> multisetAuthorsIterator;
        HashSet<Author> setAuthors;



        Document currDoc;
        Author currAuthor;

        Iterator<MapLabels> setMapLabelsIterator;



        // *********
        // create a map of docs with (ids, docs)
        // *********
        mapDocs = new HashMap();
        int docCounter = 0;
        for (Document element : ControllerBean.setDocs) {
            docCounter++;
            element.setDocId(docCounter);
            mapDocs.put(docCounter, element);
        }

        // *********
        // create a map of authors to dates
        // *********
        mapAuthorNameToDates = new HashMap();


        // *********
        // put the labels in the set into a more convenient map<String,String> form
        // *********
        setMapLabelsIterator = ControllerBean.setMapLabels.iterator();
        MapLabels currMapLabels;
        mapIncorrectToCorrect = new HashMap();
        String[] terms;
        String currOriginalFullname;
        while (setMapLabelsIterator.hasNext()) {
            currMapLabels = setMapLabelsIterator.next();
            terms = currMapLabels.getLabel1().split(",");
            currOriginalFullname = terms[1].trim() + " " + terms[0].trim();
            System.out.println("currOriginalFullname" + currOriginalFullname);
            System.out.println("currMapLabels.getLabel2(): \"" + currMapLabels.getLabel2() + "\"");
            mapIncorrectToCorrect.put(currOriginalFullname, currMapLabels.getLabel2());
        }


        // *********
        // loop through the docs in mapDocs. For each doc:
        //    1. put the correct name as a FullnameWithComms
        //    2. update the map of dates
        //    3. reinput the doc in the map
        // *********

        mapDocsIterator = mapDocs.keySet().iterator();
        while (mapDocsIterator.hasNext()) {
            int currDocId = mapDocsIterator.next();
            currDoc = mapDocs.get(currDocId);
            currSetAuthors = currDoc.getAuthors();
            currSetAuthorsIterator = currSetAuthors.iterator();
            bufferSetAuthor = new HashSet();
            while (currSetAuthorsIterator.hasNext()) {
                int currStart;
                int currEnd;
                currAuthor = currSetAuthorsIterator.next();
                if (currAuthor.getFullname().equals(ControllerBean.getSearch().getFullname())) {
                    continue;
                }
                System.out.println("currAuthor fullname with comma is: " + currAuthor.getFullname());
                System.out.println("correct version is: " + mapIncorrectToCorrect.get(currAuthor.getFullname()));
                currAuthor.setFullnameWithComma(mapIncorrectToCorrect.get(currAuthor.getFullname()));
                if (mapAuthorNameToDates.containsKey(currAuthor.getFullname())) {
                    currStart = Math.min(mapAuthorNameToDates.get(currAuthor.getFullname()).getLeft(), currAuthor.getYearFirstCollab());
                    currEnd = Math.max(mapAuthorNameToDates.get(currAuthor.getFullname()).getRight(), currAuthor.getYearLastCollab());
                    mapAuthorNameToDates.put(currAuthor.getFullname(), new Pair(currStart, currEnd));
                } else {
                    mapAuthorNameToDates.put(currAuthor.getFullname(), new Pair(currAuthor.getYearFirstCollab(), currAuthor.getYearLastCollab()));
                }
                bufferSetAuthor.add(currAuthor);
            }
            currDoc.setAuthors(bufferSetAuthor);
            mapDocs.put(currDocId, currDoc);

        }


        //   ***********
        //   extracts multiset of Authors
        //   ***********

        mapDocsIterator = mapDocs.keySet().iterator();
        multisetAuthors = HashMultiset.create();
        while (mapDocsIterator.hasNext()) {
            int currDocId = mapDocsIterator.next();
            currDoc = mapDocs.get(currDocId);
            currSetAuthors = currDoc.getAuthors();
            currSetAuthorsIterator = currSetAuthors.iterator();
            while (currSetAuthorsIterator.hasNext()) {
                multisetAuthors.add(currSetAuthorsIterator.next());
            }

        }


        //   ***********
        //   loops through multiset of Authors
        //   for each of them, update start and end date, and count them
        //   ***********

        setAuthors = new HashSet();
        multisetAuthorsIterator = multisetAuthors.elementSet().iterator();
        while (multisetAuthorsIterator.hasNext()) {
            currAuthor = multisetAuthorsIterator.next();
            currAuthor.setYearFirstCollab(mapAuthorNameToDates.get(currAuthor.getFullname()).getLeft());
            currAuthor.setYearLastCollab(mapAuthorNameToDates.get(currAuthor.getFullname()).getLeft());
            currAuthor.setTimesMentioned(multisetAuthors.count(currAuthor));
            setAuthors.add(currAuthor);
        }

        return setAuthors;
    }
}
