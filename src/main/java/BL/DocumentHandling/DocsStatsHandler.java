/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.DocumentHandling;

import Controller.ControllerBean;
import Model.Document;
import Utils.Pair;
import com.google.common.collect.HashMultiset;
import java.util.Iterator;

/**
 *
 * @author C. Levallois
 */
public class DocsStatsHandler {

    public void computeNumberDocs() {

        ControllerBean.nbDocs = ControllerBean.setDocs.size();
    }

    public Pair<String, Integer> extractMostFrequentSource() {
        HashMultiset multisetTitles = HashMultiset.create();
        Iterator<Document> setDocsIterator = ControllerBean.setDocs.iterator();
        Document currDoc;
        while (setDocsIterator.hasNext()) {
            currDoc = setDocsIterator.next();
            if (currDoc.getPublication_outlet() != null) {
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
