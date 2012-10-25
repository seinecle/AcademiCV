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
    //in debugging mode, the mendeley pubs of Andrea Scharnhorst are fetched locally
    static boolean mendeley_in_debugging_mode = true;
    
    // ##### ARXIV API CALL DEBUGGING
    //in debugging mode, the arxiv pubs of Andrea Scharnhorst are fetched locally
    static boolean arxiv_in_debugging_mode = true;
    
    // ##### SCOPUS API CALL DEBUGGING
    static boolean scopus_in_debugging_mode = true;
    
    // ##### WORLDCAT API CALL DEBUGGING
    //in debugging mode, the worldcat identities of Andrea Scharnhorst are fetched locally
    static boolean worldcat_in_debugging_mode = false;
    
    // ##### NEW YORK TIMES API CALL DEBUGGING
    //in debugging mode, no call to NYT is made, at all
    static boolean nyt_in_debugging_mode = true;
    
    // ##### WISDOM CROWDS DEBUGGING #################################
    // in debugging mode, user edits are persisted in the db BUT they are not retrieved in the disambiguation phase
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
    
    public static boolean scopusDebugStateTrueOrFalse() {
        return scopus_in_debugging_mode;
    }
    
    public static boolean nytDebugStateTrueOrFalse() {
        return nyt_in_debugging_mode;
    }
    
    public static boolean worldcatDebugStateTrueOrFalse() {
        return worldcat_in_debugging_mode;
    }
    
    
}
