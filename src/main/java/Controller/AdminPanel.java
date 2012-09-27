/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

/**
 *
 * @author C. Levallois
 */
public class AdminPanel {

    // ##### MENDELEY API CALL DEBUGGING #################################
    static boolean mendeley_in_debugging_mode = false;
    // ###################################################################  

    // ##### WISDOM CROWDS DEBUGGING #################################
    static boolean wisdom_crowds_in_debugging_mode = true;
    // ###################################################################  


       
    
    public static boolean mendeleyDebugStateTrueOrFalse() {
        return mendeley_in_debugging_mode;
    }

    public static boolean wisdomCrowdsDebugStateTrueOrFalse() {
        return wisdom_crowds_in_debugging_mode;
    }
}
