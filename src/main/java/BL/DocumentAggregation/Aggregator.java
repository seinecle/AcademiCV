/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.DocumentAggregation;

import Controller.ControllerBean;
import Model.Document;
import Utils.FindAllPairs;
import Utils.Pair;
import Utils.WeightedLevenstheinDistanceCalculator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author C. Levallois
 */
public class Aggregator {

    private static HashSet<Document> setDocs;
    private static Set<Pair<Document, Document>> setPairsDocs;

    Aggregator() {
    }

    public static void aggregate() {
        setDocs = new HashSet();
        setDocs.addAll(ControllerBean.ds.find(Document.class).field("uuid").equal(ControllerBean.uuid.toString()).asList());
        setPairsDocs = new FindAllPairs().getAllPairs(setDocs);

        Iterator<Pair<Document, Document>> setPairsDocsIterator = setPairsDocs.iterator();
        Document mergedDoc;
        while (setPairsDocsIterator.hasNext()) {

            Pair<Document, Document> currPair = setPairsDocsIterator.next();
            float distance = WeightedLevenstheinDistanceCalculator.compute(currPair.getLeft().getTitle(), currPair.getRight().getTitle());
            
            //if two docs are very smilar (almost identical title, same year), keep the one from Mendeley as it has more meta info.
            // this should be changed when more APIs will be added
            if (distance > 0.95 & currPair.getLeft().getYear() == currPair.getRight().getYear()) {
                
                mergedDoc = DocumentMerger.merge(currPair.getLeft(), currPair.getRight());
                setDocs.remove(currPair.getLeft());
                setDocs.remove(currPair.getRight());
                setDocs.add(mergedDoc);
            }

        }
    }
}
