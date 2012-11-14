/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.APIs.Mendeley;

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
public class MendeleyAPIController implements Callable, Serializable {

    private ContainerMendeleyDocuments mendeleyDocs;
    private Author search;
    
    public MendeleyAPIController(Author search){
        this.search = search;
    }

    @Override
    public Integer call() throws Exception {

        Clock gettingMendeleyData = new Clock("calling Mendeley...");
        mendeleyDocs = MendeleyAPICaller.run(search.getForename(), search.getSurname());
        ProgressBarMessenger.setProgress("mendeley returned");
        MendeleyAPIresponseParser parser = new MendeleyAPIresponseParser(mendeleyDocs, search);
        parser.parse();
        gettingMendeleyData.closeAndPrintClock();

        return null;
    }
}
