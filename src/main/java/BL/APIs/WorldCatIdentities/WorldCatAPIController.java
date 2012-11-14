/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.APIs.WorldCatIdentities;

import Controller.ControllerBean;
import Utils.Clock;
import View.ProgressBarMessenger;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.Callable;
import org.xml.sax.InputSource;
import Model.Author;

/**
 *
 * @author C. Levallois
 */
public class WorldCatAPIController implements Callable, Serializable {

    private InputSource worldcatInputSource;
    private int currBirthYear;
    private Author search;

    public WorldCatAPIController(Author search) {
        this.search = search;
    }

    @Override
    public Integer call() throws Exception {
        Clock gettingWorldCat = new Clock("calling WorldCat...");

        currBirthYear = 0;
        worldcatInputSource = WorldCatAPICaller.run(search.getForename(), search.getSurname());
        HashSet<String> setIdentities = new WorldCatAPIresponseParser(worldcatInputSource,search).parse();
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
            worldcatIdentitiesParser.parse();
        }

        System.out.println("setting Temp Birth Year: " + currBirthYear);
        ProgressBarMessenger.setProgress("worldcat returned");

        gettingWorldCat.closeAndPrintClock();

        return 0;

    }

    public int getCurrBirthYear() {
        return currBirthYear;
    }

    public void setCurrBirthYear(int newCurrBirthYear) {
        currBirthYear = newCurrBirthYear;
    }
}
