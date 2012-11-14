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

/**
 *
 * @author C. Levallois
 */

public class NYTAPIController implements Callable, Serializable {

    private static ContainerNYTDocuments NYTDocs;

    public NYTAPIController() {
    }

    @Override
    public Integer call() throws Exception {

        Clock gettingNYTData = new Clock("calling the NYT...");
        NYTDocs = NYTAPICaller.callAPI(ControllerBean.getSearch().getForename(), ControllerBean.getSearch().getSurname());
        ControllerBean.setNYTfound(!NYTDocs.getDocuments().isEmpty());
        ProgressBarMessenger.setProgress("nyt returned");
        System.out.println("nyt returned");

        gettingNYTData.closeAndPrintClock();
        return 0;

    }

    public static ContainerNYTDocuments getNYTDocs() {
        return NYTDocs;
    }
    
    
}
