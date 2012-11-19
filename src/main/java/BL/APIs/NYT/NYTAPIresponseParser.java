package BL.APIs.NYT;

import BL.APIs.Mendeley.*;
import Controller.ControllerBean;
import Model.Author;
import Model.Document;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.faces.bean.ManagedProperty;

/**
 *
 * @author C. Levallois
 */
public class NYTAPIresponseParser {

    ContainerNYTDocuments container;
    private Set<Document> setNYTDocs = new HashSet();
    

    public NYTAPIresponseParser(ContainerNYTDocuments container) {
        this.container = container;
    }

    public Set<Document> parse() {
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
            if (currDoc.getTitle() == null || currDoc.getDate() == null || currDoc.getYear() == null) {
                continue;
            }
            newDoc = new Document();
            newDoc.setTitle(currDoc.getTitle());
            newDoc.setNyt_url(currDoc.getUrl());
            newDoc.setPublication_outlet("New York Times");
            newDoc.setWhereFrom("New York Times API");
            newDoc.setYear(Integer.parseInt(currDoc.getYear()));
            setNYTDocs.add(newDoc);
        }
        return setNYTDocs;
        
    }
}
