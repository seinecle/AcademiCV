/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import java.io.Serializable;
import java.util.Date;
import org.bson.types.ObjectId;

@Entity
public class Search implements Serializable {

    @Id
    private ObjectId id;
    private String forename;
    private String surname;
    private String fullname;
    private String fullnameWithComma;
    private Date date;
    private String uuid;

    public Search() {
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Date getDate() {
        return date;
    }

    public void setDate() {
        this.date = new Date();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFullname() {
        return forename + " " + surname;
    }

    public String getFullnameWithComma() {
        return surname + ", " + forename;
    }
}
