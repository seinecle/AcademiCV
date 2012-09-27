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
public class ProcessingBean implements Serializable {

    private String json;

    public ProcessingBean() {
        this.json = ControllerBean.getJson();

    }

    public String getJson() {
        return json;
    }

}
