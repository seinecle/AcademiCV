/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.APIs.Arxiv;

import Controller.ControllerBean;
import Utils.Clock;
import View.ProgressBarMessenger;
import java.io.Serializable;
import java.util.concurrent.Callable;
import org.xml.sax.InputSource;

/**
 *
 * @author C. Levallois
 */
public class ArxivAPIController implements Callable, Serializable {

    public ArxivAPIController() {
    }

    @Override
    public Integer call() throws Exception {
        Clock gettingArxivData = new Clock("calling Arxiv...");
        InputSource readerArxivResults = ArxivAPICaller.run(ControllerBean.getSearch().getForename(), ControllerBean.getSearch().getSurname());
        ProgressBarMessenger.setProgress("arxiv returned");
        ArxivAPIresponseParser parser = new ArxivAPIresponseParser(readerArxivResults);
        parser.parse();
        System.out.println("nb Arxiv docs found: " + ControllerBean.nbArxivDocs);
        gettingArxivData.closeAndPrintClock();

        return null;
    }
}
