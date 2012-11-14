/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.APIs.NYT;

import Controller.ControllerBean;
import Utils.Clock;
import Model.Document;
import View.ProgressBarMessenger;
import java.io.Serializable;
import java.util.concurrent.Callable;
import Model.Author;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author C. Levallois
 */
public class NYTAPIController implements Callable, Serializable {

    private ContainerNYTDocuments NYTDocs;
    private Author search;

    public NYTAPIController(Author search) {
        this.search = search;
    }

    @Override
    public Set<Document> call() throws Exception {

        Set<Document> setNYTDocs = new HashSet();
        Clock gettingNYTData = new Clock("calling the NYT...");
        NYTDocs = NYTAPICaller.callAPI(search.getForename(), search.getSurname());
        NYTAPIresponseParser NYTparser = new NYTAPIresponseParser(NYTDocs);
        setNYTDocs = NYTparser.parse();
        ProgressBarMessenger.setProgress("nyt returned");
        System.out.println("nyt returned");

        gettingNYTData.closeAndPrintClock();
        System.out.println("about to return the set of NYT docs, size is: " + setNYTDocs.size());
        return setNYTDocs;

    }
}
