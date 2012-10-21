/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.APIs.Mendeley;

import BL.APIs.Mendeley.MendeleyDocument.author;
import Controller.ControllerBean;
import Model.Author;
import Model.Document;
import java.util.ArrayList;
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
        List<MendeleyDocument> currList;

        // this if else deals with the case when the container of Mendeley docs is empty, bc the Mendeley API was unresponsive
        // if the container is null, just create an empty list
        if (container != null) {
            currList = container.getDocuments();
        } else {
            currList = new ArrayList();

        }
        Iterator<MendeleyDocument> currListIterator = currList.iterator();
        Iterator<author> currDocAuthorsIterator;
        Document newDoc;
        MendeleyDocument currDoc;
        HashSet<Author> setcurrAuthors;
        ControllerBean.nbMendeleyDocs = currList.size();
        while (currListIterator.hasNext()) {


            currDoc = currListIterator.next();

            //this skips the documents that have incomplete data. They will simply not be considered by the application.
            if (currDoc.getTitle() == null || currDoc.getAuthors() == null || currDoc.getYear() == null) {
                continue;
            }
            newDoc = new Document();

            currDocAuthorsIterator = currDoc.getAuthors().iterator();
            setcurrAuthors = new HashSet();
            author currauthor;

            while (currDocAuthorsIterator.hasNext()) {
                currauthor = currDocAuthorsIterator.next();
                if ("".equals(currauthor.forename.trim()) || "".equals(currauthor.surname.trim())) {
                    continue;
                }
                if (ControllerBean.getSearch().getForename().equals(currauthor.forename.trim()) && ControllerBean.getSearch().getSurname().equals(currauthor.surname.trim())) {
                    continue;
                }
                setcurrAuthors.add(new Author(currauthor.forename, currauthor.surname, ControllerBean.uuid));
//                System.out.println("currauthor.forename in Mendeley parser: " + currauthor.getForename());
//                System.out.println("currauthor.surname in Mendeley parser: " + currauthor.getSurname());
            }
            newDoc.setAuthors(setcurrAuthors);

            newDoc.setDoi(currDoc.getDoi());
            newDoc.setTitle(currDoc.getTitle());
            newDoc.setMendeley_url(currDoc.getMendeley_url());
            newDoc.setPublication_outlet(currDoc.getPublication_outlet());
            newDoc.setUuid(ControllerBean.uuid);
            newDoc.setWhereFrom("mendeley");
            newDoc.setYear(Integer.parseInt(currDoc.getYear()));


            ControllerBean.setDocs.add(newDoc);

        }
    }
}
