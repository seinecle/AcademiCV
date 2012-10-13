/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.APIs.Mendeley;

import BL.APIs.Mendeley.MendeleyDocument.author;
import Controller.ControllerBean;
import Model.Author;
import Model.Document;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author C. Levallois
 */
public class MendeleyAPIresponseParser {

    ContainerMendeleyDocuments container;

    public MendeleyAPIresponseParser(ContainerMendeleyDocuments container) {
        this.container = container;
    }

    public void parse() {

        List<MendeleyDocument> currList = container.getDocuments();
        Iterator<MendeleyDocument> currListIterator = currList.iterator();
        Iterator<author> currDocAuthorsIterator;
        Document newDoc;
        MendeleyDocument currDoc;
        HashSet<Author> setcurrAuthors;
        while (currListIterator.hasNext()) {


            currDoc = currListIterator.next();

            //this skips the documents that have incomplete data. They will simply not be considered by the application.
            if (currDoc.getTitle() == null || currDoc.getAuthors() == null || currDoc.getYear() == null) {
                continue;
            }
            newDoc = new Document();

            currDocAuthorsIterator = currDoc.getAuthors().iterator();
            setcurrAuthors = new HashSet();
            while (currDocAuthorsIterator.hasNext()) {
                author currauthor = currDocAuthorsIterator.next();
                setcurrAuthors.add(new Author(currauthor.forename, currauthor.surname, ControllerBean.uuid));
            }
            newDoc.setAuthors(setcurrAuthors);

            newDoc.setDoi(currDoc.getDoi());
            newDoc.setTitle(currDoc.getTitle());
            newDoc.setMendeley_url(currDoc.getMendeley_url());
            newDoc.setPublication_outlet(currDoc.getPublication_outlet());
            newDoc.setUuid(ControllerBean.uuid);
            newDoc.setWhereFrom("mendeley");

            ControllerBean.ds.save(newDoc);

        }
    }
}
