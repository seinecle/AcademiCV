/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.APIs.WorldCatIdentities;

import Model.Author;
import Model.Document;
import Utils.Clock;
import Utils.PairSimple;
import View.ProgressBarMessenger;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.InputSource;

/**
 *
 * @author C. Levallois
 */
public class WorldCatAPIController implements Callable, Serializable {

    private InputSource worldcatInputSource;
    private int currBirthYear;
    private Author search;
    private Set<Document> setWorldCatDocs;
    

    public WorldCatAPIController(Author search) {
        this.search = search;
    }

    @Override
    public PairSimple<Set<Document>,Author> call() {
        try {
            Clock gettingWorldCat = new Clock("calling WorldCat...");
            setWorldCatDocs = new HashSet();
            currBirthYear = 0;
            worldcatInputSource = WorldCatAPICaller.run(search.getForename(), search.getSurname());
            PairSimple<HashSet<String>, Author> ps = new WorldCatAPIresponseParser(worldcatInputSource, search).parse();
            HashSet<String> setIdentities = ps.getLeft();
            if (ps.getRight().getBirthYear() != null) {
                search.setBirthYear(ps.getRight().getBirthYear());
            }
            System.out.println("nb of identities found: " + setIdentities.size());
            //        ProgressBarMessenger.updateMsg("<p>hello from the worldcatAPI call!</p>");
            ProgressBarMessenger.setProgress("worldcat in progress");

            Iterator<String> setIdentitiesIterator = setIdentities.iterator();
            String currIdentity;
            while (setIdentitiesIterator.hasNext()) {
                currIdentity = setIdentitiesIterator.next();
                System.out.println("launching a second phase of WorldCat call: looking at the identity: " + currIdentity);
                worldcatInputSource = WorldCatAPICallerWithIdentityCode.run(currIdentity);
                WorldCatAPIIdentityresponseParser worldcatIdentitiesParser = new WorldCatAPIIdentityresponseParser(worldcatInputSource, search);
                setWorldCatDocs.addAll(worldcatIdentitiesParser.parse());
            }

            System.out.println("setting Temp Birth Year: " + currBirthYear);
            ProgressBarMessenger.setProgress("worldcat returned");

            gettingWorldCat.closeAndPrintClock();

        } catch (Exception ex) {
            Logger.getLogger(WorldCatAPIController.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("about to return the set of Worldcat docs, size is: " + setWorldCatDocs.size());
        return new PairSimple(setWorldCatDocs,search);

    }

    public int getCurrBirthYear() {
        return currBirthYear;
    }

    public void setCurrBirthYear(int newCurrBirthYear) {
        currBirthYear = newCurrBirthYear;
    }
}
