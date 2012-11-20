/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.DocumentHandling;

import Model.Document;
import Utils.Pair;
import com.google.common.collect.HashMultiset;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author C. Levallois
 */
public class DocsStatsHandler {

    public Pair<String, Integer> extractMostFrequentSource(Set<Document> setDocs) {
        HashMultiset multisetTitles = HashMultiset.create();
        Iterator<Document> setDocsIterator = setDocs.iterator();
        Document currDoc;
        while (setDocsIterator.hasNext()) {
            currDoc = setDocsIterator.next();
            if (currDoc.getPublication_outlet() != null && !currDoc.getPublication_outlet().equals("publisher not identified")) {
                multisetTitles.add(currDoc.getPublication_outlet());
            }
        }
        Iterator<String> multisetTitlesIterator = multisetTitles.elementSet().iterator();
        String currTitle;
        int maxCountTitle = 0;
        String mostFrequentTitle = "";
        while (multisetTitlesIterator.hasNext()) {
            currTitle = multisetTitlesIterator.next();
            if (multisetTitles.count(currTitle) > maxCountTitle) {
                maxCountTitle = Math.max(maxCountTitle, multisetTitles.count(currTitle));
                mostFrequentTitle = currTitle;
            }
        }
        System.out.println("most frequent source: " + mostFrequentTitle);
        System.out.println("count: " + maxCountTitle);
        return new Pair(mostFrequentTitle, maxCountTitle);

    }
}
