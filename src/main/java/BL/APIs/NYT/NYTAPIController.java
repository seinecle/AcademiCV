/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.APIs.NYT;

import Controller.ControllerBean;
import Utils.Clock;
import View.ProgressBarMessenger;
import java.io.Serializable;
import java.util.concurrent.Callable;
import Model.Author;
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
    public Integer call() throws Exception {

        Clock gettingNYTData = new Clock("calling the NYT...");
        NYTDocs = NYTAPICaller.callAPI(search.getForename(), search.getSurname());
        ProgressBarMessenger.setProgress("nyt returned");
        System.out.println("nyt returned");

        gettingNYTData.closeAndPrintClock();
        return 0;

    }

    public ContainerNYTDocuments getNYTDocs() {
        return NYTDocs;
    }
    
    
}
