/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Controller.ControllerBean;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author C. Levallois
 */
@ManagedBean
@ViewScoped
public class ReportBean implements Serializable {

    private String json;
    private String nameClicked;

    public ReportBean() {
        this.json = ControllerBean.getJson();

    }

    public String getJson() {
        return json;
    }

    public String getNameClicked() {
        return nameClicked;
    }

    public void setNameClicked(String nameClicked) {
        this.nameClicked = nameClicked;
    }
}
