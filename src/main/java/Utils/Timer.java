/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import org.joda.time.LocalDateTime;

/**
 *
 * @author C. Levallois
 */
public class Timer {

    static private LocalDateTime refDateTime;
    static private LocalDateTime currDateTime;
    static private boolean continueWaiting;

    public static void waitSeconds(Integer seconds) {

        continueWaiting = true;
        refDateTime = new LocalDateTime();
        while (continueWaiting) {
            currDateTime = new LocalDateTime();
            if (currDateTime.isAfter(refDateTime.plusSeconds(seconds))) {
                continueWaiting = false;
            }
        }

    }
}
