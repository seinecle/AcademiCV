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

    public static void run() throws Exception {
        worldcatInputSource = WorldCatAPICaller.run(ControllerBean.getSearch().getForename(), ControllerBean.getSearch().getSurname());
        HashSet<String> setIdentities = new WorldCatAPIresponseParser(worldcatInputSource).parse();

        Iterator<String> setIdentitiesIterator = setIdentities.iterator();

        while (setIdentitiesIterator.hasNext()) {
            String currIdentity = setIdentitiesIterator.next();
            worldcatInputSource = WorldCatAPICallerWithIdentityCode.run(currIdentity);
            new WorldCatAPIIdentityresponseParser(worldcatInputSource).parse();
        }

        ControllerBean.setTempBirthYear(currBirthYear);

    }

    public static int getCurrBirthYear() {
        return currBirthYear;
    }

    public static void setCurrBirthYear(int currBirthYear) {
        currBirthYear = currBirthYear;
    }
}
