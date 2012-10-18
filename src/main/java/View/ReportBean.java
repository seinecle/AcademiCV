/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Controller.ControllerBean;
import java.io.Serializable;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author C. Levallois
 */
@ManagedBean
@ViewScoped
public class ReportBean implements Serializable {

    private String json;
    private String nameClicked = "test";
    private String nameViaRemoteCommand;
    private String countDocsCurrNameClicked;

    public ReportBean() {
        this.json = ControllerBean.getJson();

    }

    public String getJson() {
        return json;
    }

    public String getNameClicked() {
        return nameViaRemoteCommand;
    }

    public void setNameClicked(String nameClicked) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map map = context.getExternalContext().getRequestParameterMap();
        this.nameClicked = (String) map.get("nameClicked");

    }

    public void passName() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map map = context.getExternalContext().getRequestParameterMap();
        this.nameClicked = (String) map.get("nameClicked");
        this.nameViaRemoteCommand = (String) map.get("nameClicked");
        this.countDocsCurrNameClicked = (String) map.get("countDocs");


        FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().put("clickedAuthor", this.nameViaRemoteCommand);
        FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().put("uuid", ControllerBean.uuid.toString());


        //        System.out.println("name retrieved from the map: " + (String) map.get("nameClicked"));
//        System.out.println("setting the name via remoteCommand, name is: " + this.nameViaRemoteCommand);

    }

    public void writeNameClicked(String nameClicked) {
//        System.out.println("setting the name via method, name is: " + nameClicked);
        this.nameClicked = nameClicked;

    }

    public String getCountDocsCurrNameClicked() {
        return countDocsCurrNameClicked;
    }

    public String obtainFullName() {
        String[] fields = this.nameClicked.split(",");
        if (this.nameClicked.contains(",")) {
            return fields[1] + " " + fields[0];
        } else {
            return this.nameClicked;
        }
    }

    public void setCountDocsCurrNameClicked() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map map = context.getExternalContext().getRequestParameterMap();
        System.out.println("count is: " + this.countDocsCurrNameClicked);
    }
    
    
}
