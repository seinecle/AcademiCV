/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Controller.ControllerBean;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author C. Levallois
 */
@ManagedBean
@ViewScoped
public class MapLabels implements Comparable<MapLabels>, Serializable {

    private Author author1;
    private Author author2;
    private String author2Edited;
    private String author1displayed;
    private String author2displayed;
    private String uuid;
    private boolean editable;
    private Query<PersistingEdit> updateQuery;
    private UpdateOperations<PersistingEdit> ops;
    private boolean deleted;
    private Query<GlobalEditsCounter> updateQueryCounter;
    private UpdateOperations<GlobalEditsCounter> opsCounter;

    public MapLabels() {
    }

    public MapLabels(Author author1, Author author2, String uuid) {
        this.author1 = author1;
        this.author2 = author2;
        this.author2Edited = author2.getFullnameWithComma();
        this.uuid = uuid;
    }

    public MapLabels(String author1displayed, String author2displayed) {
        this.author1displayed = author1displayed;
        this.author2displayed = author2displayed;
    }

    public Author getAuthor1() {
        return author1;
    }

    public void setAuthor1(Author author1) {
        this.author1 = author1;
    }

    public Author getAuthor2() {
        return author2;
    }

    public void setAuthor2(Author author2) {
        this.author2 = author2;
    }

    public void setUuid(String Uuid) {
        this.uuid = Uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getAuthor1displayed() {
        return author1.getFullnameWithComma();
    }

    public void setAuthor1displayed(String author1displayed) {
        this.author1.setFullname(author1displayed);
    }

    public String getAuthor2displayed() {
        return author2.getFullnameWithComma();
    }

    public void setAuthor2displayed(String author2displayed) {
        this.author2.setFullnameWithComma(author2displayed);
    }

    public String getAuthor2Edited() {
        return author2Edited;
    }

    public void setAuthor2Edited(String author2Edited) {
        this.author2Edited = author2Edited;

        updateQuery = ControllerBean.ds.createQuery(PersistingEdit.class).field("reference").equal(ControllerBean.getSearch().getFullnameWithComma());
        updateQuery.field("originalForm").equal(this.author2.getFullnameWithComma());
        updateQuery.field("editedForm").equal(this.author2Edited);
        ops = ControllerBean.ds.createUpdateOperations(PersistingEdit.class).inc("counter", 1);
        ControllerBean.ds.update(updateQuery, ops, true);

        updateQueryCounter = ControllerBean.ds.createQuery(GlobalEditsCounter.class);
        opsCounter = ControllerBean.ds.createUpdateOperations(GlobalEditsCounter.class).inc("globalCounter", 2);
        ControllerBean.ds.update(updateQueryCounter, opsCounter, true);
        ControllerBean.pushCounter();


        this.author2.setFullnameWithComma(author2Edited);

    }

    @Override
    public int compareTo(MapLabels o) {
//        return this.author2displayed.compareTo(o.getAuthor2displayed());
        return this.author2.getFullnameWithComma().compareTo(o.getAuthor2().getFullnameWithComma());
    }
    
    
}
