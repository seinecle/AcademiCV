package BL.APIs.NYT;

import BL.APIs.Mendeley.*;
import Controller.ControllerBean;
import Model.Author;
import Model.Document;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.faces.bean.ManagedProperty;

/**
 *
 * @author C. Levallois
 */
public class NYTAPIresponseParser {

    ContainerNYTDocuments container;
    private int nbNYTDocs = 0;
    private Author search;
    @ManagedProperty("#{controllerBean}")
    private ControllerBean controllerBean;

    public void setcontrollerBean(ControllerBean controllerBean) {
        this.controllerBean = controllerBean;
    }

    public NYTAPIresponseParser(ContainerNYTDocuments container, Author search) {
        this.container = container;
        this.search = search;
    }

    public void parse() {
        List<NYTDoc> currList;

        // this if else deals with the case when the container of Mendeley docs is empty, bc the Mendeley API was unresponsive
        // if the container is null, just create an empty list
        if (container != null) {
            currList = container.getDocuments();
        } else {
            currList = new ArrayList();

        }
        Iterator<NYTDoc> currListIterator = currList.iterator();
        Document newDoc;
        NYTDoc currDoc;
        while (currListIterator.hasNext()) {
            currDoc = currListIterator.next();

            //this skips the documents that have incomplete data. They will simply not be considered by the application.
            if (currDoc.getTitle() == null || currDoc.getDate() == null) {
                continue;
            }
            newDoc = new Document();
            newDoc.setTitle(currDoc.getTitle());
            newDoc.setNyt_url(currDoc.getUrl());
            newDoc.setPublication_outlet("New York Times");
            newDoc.setWhereFrom("New York Times API");
            newDoc.setYear(Integer.parseInt(currDoc.getYear()));
            nbNYTDocs++;
            controllerBean.addToSetMediaDocs(newDoc);
        }
    }
}
