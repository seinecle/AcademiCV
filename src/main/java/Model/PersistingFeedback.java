/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Controller.ControllerBean;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.bson.types.ObjectId;

@ManagedBean
@Entity
public class PersistingFeedback implements Serializable {

    @Id
    private ObjectId id;
    private String comment;
  
    public PersistingFeedback() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

 
    
}
