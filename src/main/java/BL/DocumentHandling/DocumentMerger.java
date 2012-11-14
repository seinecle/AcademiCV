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

    Document merge(Document doc1, Document doc2) {
        Document mergedDoc = new Document();

        //set Title
        mergedDoc.setTitle(doc1.getTitle());

        //set Authors
        mergedDoc.setAuthors(new AuthorMerger().mergeAuthorSets(doc1.getAuthors(), doc2.getAuthors()));

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

        //set summary
        if (doc1.getSummary() == null) {
            mergedDoc.setSummary(doc2.getSummary());
        } else {
            mergedDoc.setSummary(doc1.getSummary());
        }

        //set language
        if (doc1.getLanguage() == null) {
            mergedDoc.setLanguage(doc2.getLanguage());
        } else {
            mergedDoc.setLanguage(doc1.getLanguage());
        }

        //set type
        //here we use a conservative approach,
        //because we know that worldcat returns "books" for articles, for instance
        //so unless we compare 2 docs and we know both are of the same type, we don't rule for one against another.
        if (doc1.getTypeOutlet() == null ? doc2.getTypeOutlet() == null : doc1.getTypeOutlet().equals(doc2.getTypeOutlet())) {
            mergedDoc.setTypeOutlet(doc2.getTypeOutlet());
        } else {
            //nothing. We leave the type unknown
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
