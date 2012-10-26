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
        Iterator<Author> setAuthorsIterator;



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
//            System.out.println("currOriginalFullname" + currOriginalFullname);
//            System.out.println("currMapLabels.getLabel2(): \"" + currMapLabels.getLabel2() + "\"");
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
//                System.out.println("currAuthor fullname with comma is: " + currAuthor.getFullname());
//                System.out.println("correct version is: " + mapIncorrectToCorrect.get(currAuthor.getFullname()));
//                System.out.println("doc year is: " + currDoc.getYear());

                //this deals with the case when an author from docs has been deleted by the user. It won't be found in the mapIncorrectToCorrect
                //so a NPE will be thrown. Here, we prevent that and just skip this author.

                if (mapIncorrectToCorrect.get(currAuthor.getFullname()) == null) {
                    continue;
                }
                currAuthor.setFullnameWithComma(mapIncorrectToCorrect.get(currAuthor.getFullname()));
                if (mapAuthorNameToDates.containsKey(currAuthor.getFullname())) {
//                    System.out.println("we are in the modif of an existing key / value pair");
//                    System.out.println("currStart before: " + mapAuthorNameToDates.get(currAuthor.getFullname()).getLeft());
//                    System.out.println("currEnd before: " + mapAuthorNameToDates.get(currAuthor.getFullname()).getRight());

                    currStart = Math.min(mapAuthorNameToDates.get(currAuthor.getFullname()).getLeft(), currDoc.getYear());
                    currEnd = Math.max(mapAuthorNameToDates.get(currAuthor.getFullname()).getRight(), currDoc.getYear());
//                    System.out.println("currStart: " + currStart);
//                    System.out.println("currEnd: " + currEnd);

                    mapAuthorNameToDates.put(currAuthor.getFullname(), new Pair(currStart, currEnd));
                } else {
                    mapAuthorNameToDates.put(currAuthor.getFullname(), new Pair(currDoc.getYear(), currDoc.getYear()));
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
            currAuthor.setYearLastCollab(mapAuthorNameToDates.get(currAuthor.getFullname()).getRight());
            currAuthor.setTimesMentioned(multisetAuthors.count(currAuthor));
            setAuthors.add(currAuthor);
        }

        //-----------------------------------------------------------------------------------------------------------------------------
        // end of the aggregation work on authors. The following extracts features
        //-----------------------------------------------------------------------------------------------------------------------------


        //find most frequent co-author
        setAuthorsIterator = setAuthors.iterator();
        HashSet<Author> setMostFrequentCoAuthors = new HashSet();
        Iterator<Author> setMostFrequentCoAuthorsIterator;
        int maxSharedWorks = 0;
        while (setAuthorsIterator.hasNext()) {

            currAuthor = setAuthorsIterator.next();
            if (currAuthor.getTimesMentioned() >= maxSharedWorks) {
                maxSharedWorks = currAuthor.getTimesMentioned();
                setMostFrequentCoAuthors.add(currAuthor);
            }
        }

        setMostFrequentCoAuthorsIterator = setMostFrequentCoAuthors.iterator();
        while (setMostFrequentCoAuthorsIterator.hasNext()) {
            if (setMostFrequentCoAuthorsIterator.next().getTimesMentioned() < maxSharedWorks) {
                setMostFrequentCoAuthorsIterator.remove();
            }
        }
        ControllerBean.mostFrequentCoAuthors = setMostFrequentCoAuthors;


        //find earliest and latest dates of publication
        int earliest = 3000;
        int latest = 0;
        for (Document element : ControllerBean.setDocs) {
            if (element.getYear() < earliest) {
                earliest = element.getYear();
            }
            if (element.getYear() > latest) {
                latest = element.getYear();
            }
            ControllerBean.minYear = earliest;
            ControllerBean.maxYear = latest;
        }




        return setAuthors;
    }
}
