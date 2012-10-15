/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author C. Levallois
 */
public class WeightedLevenstheinDistanceCalculator {

    public static Float compute(String one, String two) {

        int ld = StringUtils.getLevenshteinDistance(one, two);
        float ldw = (float)ld / (float)Math.min(one.length(), two.length());
//        System.out.println("weighted distance is: "+(float) ldw);
        return ldw;


    }
}
