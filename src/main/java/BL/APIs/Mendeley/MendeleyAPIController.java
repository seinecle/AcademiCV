/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.APIs.Mendeley;

import Utils.Clock;
import View.ProgressBarMessenger;
import java.io.Serializable;
import java.util.concurrent.Callable;
import Model.Author;
import Model.Document;
import java.util.Set;

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
    public Set<Document> call() throws Exception {
        Set<Document> setMendeleyDocs;
        Clock gettingMendeleyData = new Clock("calling Mendeley...");
        mendeleyDocs = MendeleyAPICaller.run(search.getForename(), search.getSurname());
        ProgressBarMessenger.setProgress("mendeley returned");
        MendeleyAPIresponseParser parser = new MendeleyAPIresponseParser(mendeleyDocs, search);
        setMendeleyDocs = parser.parse();
        gettingMendeleyData.closeAndPrintClock();
        System.out.println("about to return the set of Mendeley docs, size is: " + setMendeleyDocs.size());

        return setMendeleyDocs;
    }
}
