/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.APIs.WorldCatIdentities;

import Controller.ControllerBean;
import java.util.HashSet;
import java.util.Iterator;
import org.xml.sax.InputSource;

/**
 *
 * @author C. Levallois
 */
public class WorldCatAPIController {

    private static InputSource worldcatInputSource;
    private static int currBirthYear;

    public WorldCatAPIController() {
    }

    public void run() throws Exception {
        currBirthYear = 0;
        worldcatInputSource = WorldCatAPICaller.run(ControllerBean.getSearch().getForename(), ControllerBean.getSearch().getSurname());
        HashSet<String> setIdentities = new WorldCatAPIresponseParser(worldcatInputSource).parse();
        System.out.println("nb of identities found: " + setIdentities.size());

        Iterator<String> setIdentitiesIterator = setIdentities.iterator();
        String currIdentity;
        while (setIdentitiesIterator.hasNext()) {
            currIdentity = setIdentitiesIterator.next();
            System.out.println("launching a second phase of WorldCat call: looking at the identity: " + currIdentity);
            worldcatInputSource = WorldCatAPICallerWithIdentityCode.run(currIdentity);
            new WorldCatAPIIdentityresponseParser(worldcatInputSource).parse();
        }

        System.out.println("setting Temp Birth Year: " + currBirthYear);
        ControllerBean.setTempBirthYear(currBirthYear);

    }

    public static int getCurrBirthYear() {
        return currBirthYear;
    }

    public static void setCurrBirthYear(int newCurrBirthYear) {
        currBirthYear = newCurrBirthYear;
    }
}
