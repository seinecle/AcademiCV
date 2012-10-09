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

//debugging    ----------------------------------------------------------------------------
    // ##### MENDELEY API CALL DEBUGGING
    static boolean mendeley_in_debugging_mode = false;
    
    // ##### ARXIV API CALL DEBUGGING
    static boolean arxiv_in_debugging_mode = true;
    
    // ##### WISDOM CROWDS DEBUGGING #################################
    static boolean wisdom_crowds_in_debugging_mode = true;
// ------------------------------------------------------------------------------------------
//parameters    ----------------------------------------------------------------------------
    // ##### WISDOM OF CROWDS - HOW MANY GLOBAL EDITS TO BE VALID
    static int minEdits = 3;

// ------------------------------------------------------------------------------------------
    public static boolean mendeleyDebugStateTrueOrFalse() {
        return mendeley_in_debugging_mode;
    }

    public static boolean wisdomCrowdsDebugStateTrueOrFalse() {
        return wisdom_crowds_in_debugging_mode;
    }

    public static int minGlobalEdits() {
        return minEdits;
    }

    public static boolean arxivDebugStateTrueOrFalse() {
        return arxiv_in_debugging_mode;
    }
    
    
}
