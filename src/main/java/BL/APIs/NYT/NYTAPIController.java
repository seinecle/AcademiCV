/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.APIs.NYT;

import Utils.Clock;
import Utils.PairSimple;
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
    public PairSimple<Set<Document>,Author> call() throws Exception {

        Set<Document> setNYTDocs;
        Clock gettingNYTData = new Clock("calling the NYT...");
        NYTDocs = NYTAPICaller.callAPI(search.getForename(), search.getSurname());
        NYTAPIresponseParser NYTparser = new NYTAPIresponseParser(NYTDocs);
        setNYTDocs = NYTparser.parse();
        ProgressBarMessenger.setProgress("nyt returned");
        System.out.println("nyt returned");

        gettingNYTData.closeAndPrintClock();
        System.out.println("about to return the set of NYT docs, size is: " + setNYTDocs.size());
        return new PairSimple(setNYTDocs,search);

    }
}
