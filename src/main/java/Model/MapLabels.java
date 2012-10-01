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

    private String label1;
    private String label2;
    private String label2Edited;
    private String uuid;
    private boolean editable;
    private Query<PersistingEdit> updateQuery;
    private UpdateOperations<PersistingEdit> ops;
    private boolean deleted;
    private Query<GlobalEditsCounter> updateQueryCounter;
    private UpdateOperations<GlobalEditsCounter> opsCounter;

    public MapLabels() {
    }

    public MapLabels(String label1, String label2, String uuid) {
        this.label1 = label1;
        this.label2 = label2;
        this.label2Edited = label2;
        this.uuid = uuid;
    }

    public MapLabels(String label1, String label2) {
        this.label1 = label1;
        this.label2 = label2;
        this.label2Edited = label2;
    }

    public String getLabel1() {
        return label1;
    }

    public void setLabel1(String label1) {
        this.label1 = label1;
    }

    public String getLabel2() {
        return label2;
    }

    public void setLabel2(String label2) {
        this.label2 = label2;
    }

    public void setUuid(String Uuid) {
        this.uuid = Uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public String getLabel2Edited() {
        return label2Edited;
    }

    public void setLabel2Edited(String label2Edited) {
        System.out.println("author edited is set: " + label2Edited);
        this.label2Edited = label2Edited;

        updateQuery = ControllerBean.ds.createQuery(PersistingEdit.class).field("reference").equal(ControllerBean.getSearch().getFullnameWithComma());
        updateQuery.field("originalForm").equal(label2);
        updateQuery.field("editedForm").equal(label2Edited);
        ops = ControllerBean.ds.createUpdateOperations(PersistingEdit.class).inc("counter", 1);
        ControllerBean.ds.update(updateQuery, ops, true);

        updateQueryCounter = ControllerBean.ds.createQuery(GlobalEditsCounter.class);
        opsCounter = ControllerBean.ds.createUpdateOperations(GlobalEditsCounter.class).inc("globalCounter", 2);
        ControllerBean.ds.update(updateQueryCounter, opsCounter, true);
        ControllerBean.pushCounter();

        this.label2 = label2Edited;

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

    @Override
    public int compareTo(MapLabels o) {
//        return this.author2displayed.compareTo(o.getAuthor2displayed());
        return this.label2.compareTo(o.getLabel2());
    }
}
