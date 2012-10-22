/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.DocumentHandling;

import Model.Document;
import Utils.FindAllPairs;
import Utils.Pair;
import Utils.WeightedLevenstheinDistanceCalculator;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author C. Levallois
 */
public class DocumentAggregator {

    private static Set<Pair<Document, Document>> setPairsDocs;

    DocumentAggregator() {
    }

    public static Set<Document> aggregate(Set<Document> setDocs) {


//        for (Document currDoc : setDocs) {
//            System.out.println("currDoc title:" + currDoc.getTitle());
//            for (Author currAuthor : currDoc.getAuthors()) {
//                System.out.println("currDoc author(s) forename :" + currAuthor.getForename());
//            }
//        }

        System.out.println("nb of docs retrieved from the db: " + setDocs.size());
        setPairsDocs = new FindAllPairs().getAllPairs(setDocs);
        System.out.println("nb of pairs: " + setPairsDocs.size());

        Iterator<Pair<Document, Document>> setPairsDocsIterator = setPairsDocs.iterator();
        Document mergedDoc;
        Pair<Document, Document> currPair;
        while (setPairsDocsIterator.hasNext()) {

            currPair = setPairsDocsIterator.next();
//            System.out.println("title 1:" + currPair.getLeft().getTitle());
//            System.out.println("title 2:" + currPair.getRight().getTitle());

            float distance = WeightedLevenstheinDistanceCalculator.compute(currPair.getLeft().getTitle(), currPair.getRight().getTitle());

            //if two docs are very smilar (almost identical title, same year), merge the two
            if (distance < 0.10f && currPair.getLeft().getYear() == currPair.getRight().getYear()) {
//                System.out.println("similar titles spotted, with same year");
//                System.out.println("title 1:" + currPair.getLeft().getTitle());
//                System.out.println("title 2:" + currPair.getRight().getTitle());

                mergedDoc = DocumentMerger.merge(currPair.getLeft(), currPair.getRight());
                setDocs.remove(currPair.getLeft());
                setDocs.remove(currPair.getRight());
                setDocs.add(mergedDoc);



            } //if two docs are very smilar but with different years, merge them but take the most recent year
            else if (distance < 0.10f && (currPair.getLeft().getYear() != currPair.getRight().getYear())) {
//                System.out.println("similar titles spotted, with different year");
//                System.out.println("title 1:" + currPair.getLeft().getTitle());
//                System.out.println("title 2:" + currPair.getRight().getTitle());
                mergedDoc = DocumentMerger.merge(currPair.getLeft(), currPair.getRight());


                if (currPair.getLeft().getYear() > currPair.getRight().getYear()) {
                    mergedDoc.setYear(currPair.getLeft().getYear());
                } else {
                    mergedDoc.setYear(currPair.getRight().getYear());
                }
                setDocs.remove(currPair.getLeft());
                setDocs.remove(currPair.getRight());
                setDocs.add(mergedDoc);

            }
        }
        System.out.println("nb of docs after aggregation / elimination of duplicates: " + setDocs.size());


        return setDocs;
    }

//    public Set<Pair<Author, Author>> getAllPairs(Set<Author> setObjects) {
//        Set<Author> setObjectsProcessed = new TreeSet<Author>();
//        Set<Pair<Author, Author>> setPairs = new TreeSet<Pair<Author, Author>>();
//        Iterator<Author> setObjectsIteratorA = setObjects.iterator();
//        Iterator<Author> setObjectsIteratorB;
//        Author currAuthorA;
//        Author currAuthorB;
//        while (setObjectsIteratorA.hasNext()) {
//            currAuthorA = setObjectsIteratorA.next();
//            setObjectsIteratorB = setObjects.iterator();
//            while (setObjectsIteratorB.hasNext()) {
//                currAuthorB = setObjectsIteratorB.next();
//                if (!setObjectsProcessed.contains(currAuthorB) && (currAuthorA.getForename() != currAuthorB.getForename() | currAuthorA.getSurname() != currAuthorB.getSurname())) {
//                    setPairs.add(new Pair(currAuthorA, currAuthorB));
//                }
//            }
//            setObjectsProcessed.add(currAuthorA);
//        }
//        return setPairs;
//
//    }
}
