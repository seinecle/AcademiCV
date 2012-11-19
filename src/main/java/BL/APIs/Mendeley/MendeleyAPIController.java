/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.APIs.Mendeley;

import Model.Author;
import Model.Document;
import Utils.Clock;
import Utils.PairSimple;
import View.ProgressBarMessenger;
import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 *
 * @author C. Levallois
 */
public class MendeleyAPIController implements Callable, Serializable {

    private ContainerMendeleyDocuments mendeleyDocs;
    private Author search;

    public MendeleyAPIController(Author search) {
        this.search = search;
    }

    @Override
    public PairSimple<Set<Document>, Author> call() throws Exception {
        Set<Document> setMendeleyDocs;
        Clock gettingMendeleyData = new Clock("calling Mendeley...");
        mendeleyDocs = MendeleyAPICaller.run(search.getForename(), search.getSurname());
        ProgressBarMessenger.setProgress("mendeley returned");
        MendeleyAPIresponseParser parser = new MendeleyAPIresponseParser(mendeleyDocs, search);
        setMendeleyDocs = parser.parse();
        gettingMendeleyData.closeAndPrintClock();
        System.out.println("about to return the set of Mendeley docs, size is: " + setMendeleyDocs.size());

        return new PairSimple(setMendeleyDocs, search);
    }
}
