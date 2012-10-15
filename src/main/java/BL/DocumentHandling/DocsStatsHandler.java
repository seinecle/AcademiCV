/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.DocumentHandling;

import Controller.ControllerBean;

/**
 *
 * @author C. Levallois
 */
public class DocsStatsHandler {
    
    public static void computeNumberDocs(){
        
        ControllerBean.nbDocs = ControllerBean.setDocs.size();
    }
    
}
