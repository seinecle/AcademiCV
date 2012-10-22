/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.DocumentHandling;

import Model.Document;

/**
 *
 * @author C. Levallois
 */
public class DocumentMerger {

    static Document merge(Document doc1, Document doc2) {
        Document mergedDoc = new Document();

        //set Title
        mergedDoc.setTitle(doc1.getTitle());

        //set Authors
        mergedDoc.setAuthors(AuthorMerger.mergeAuthorSets(doc1.getAuthors(), doc2.getAuthors()));

        //set Year
        mergedDoc.setYear(doc1.getYear());

        //set publication.outlet
        if (doc1.getPublication_outlet() == null) {
            mergedDoc.setPublication_outlet(doc2.getPublication_outlet());
        } else {
            mergedDoc.setPublication_outlet(doc1.getPublication_outlet());
        }

        //set doi
        if (doc1.getDoi() == null) {
            mergedDoc.setPublication_outlet(doc2.getDoi());
        } else {
            mergedDoc.setPublication_outlet(doc1.getDoi());
        }

        //set creation Time
        mergedDoc.setCreationDateTime(doc1.getCreationDateTime());

        //set arxiv Primary Category
        if (doc1.getTopicArxiv() != null) {
            mergedDoc.setTopicArxiv(doc1.getTopicArxiv());
        } else if (doc2.getTopicArxiv() != null) {
            mergedDoc.setTopicArxiv(doc2.getTopicArxiv());
        }

        //set where From
        mergedDoc.setWhereFrom("composite");

        return mergedDoc;
    }
}
