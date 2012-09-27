/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import java.io.Serializable;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author C. Levallois
 */
@SessionScoped
public class SearchBean implements Serializable {

    private boolean wisdomCrowds;

    /**
     * Creates a new instance of SearchBean
     */
    public SearchBean() {
    }

    public boolean isWisdomCrowds() {
        return wisdomCrowds;
    }

    public void setWisdomCrowds(boolean wisdomCrowds) {
        this.wisdomCrowds = wisdomCrowds;
    }
}
