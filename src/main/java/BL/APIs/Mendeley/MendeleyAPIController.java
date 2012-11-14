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

/**
 *
 * @author C. Levallois
 */
public class MendeleyAPIController implements Callable, Serializable {

    public ContainerMendeleyDocuments mendeleyDocs;

    @Override
    public Integer call() throws Exception {

        Clock gettingMendeleyData = new Clock("calling Mendeley...");
        mendeleyDocs = MendeleyAPICaller.run(ControllerBean.getSearch().getForename(), ControllerBean.getSearch().getSurname());
        ProgressBarMessenger.setProgress("mendeley returned");
        MendeleyAPIresponseParser parser = new MendeleyAPIresponseParser(mendeleyDocs);
        parser.parse();
        System.out.println("nb Mendeley docs found: " + ControllerBean.nbMendeleyDocs);
        gettingMendeleyData.closeAndPrintClock();

        return null;
    }
}
