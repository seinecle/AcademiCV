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
import Model.Author;
import Model.Document;
import java.util.Set;

/**
 *
 * @author C. Levallois
 */
public class ArxivAPIController implements Callable, Serializable {
    
    private Author search;

    public ArxivAPIController(Author search) {
        this.search = search;
    }

    @Override
    public Set<Document> call() throws Exception {
        Clock gettingArxivData = new Clock("calling Arxiv...");
        InputSource readerArxivResults = ArxivAPICaller.run(search.getForename(), search.getSurname());
        ProgressBarMessenger.setProgress("arxiv returned");
        ArxivAPIresponseParser parser = new ArxivAPIresponseParser(readerArxivResults);
        Set<Document> arxivDocs = parser.parse();
        gettingArxivData.closeAndPrintClock();
        System.out.println("about to return the set of Arxiv docs, size is: " + arxivDocs.size());

        return arxivDocs;
    }
}
