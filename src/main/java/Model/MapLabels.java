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
import javax.faces.bean.ManagedProperty;
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
    private String label2frozen;
    private String label3;
    private String uuid;
    private boolean editable;
    private Query<PersistingEdit> updateQuery;
    private UpdateOperations<PersistingEdit> ops;
    private boolean deleted;
    private Query<GlobalEditsCounter> updateQueryCounter;
    private UpdateOperations<GlobalEditsCounter> opsCounter;
    
    @ManagedProperty("#{controllerBean}")
    private ControllerBean controllerBean;

    public void setcontrollerBean(ControllerBean controllerBean) {
        this.controllerBean = controllerBean;
    }

    public MapLabels() {
    }

    public MapLabels(String label1, String label2, String uuid) {
        this.label1 = label1;
        this.label2 = label2;
        this.label3 = label2;
        this.uuid = uuid;
    }

    public MapLabels(String label1, String label2) {
        this.label1 = label1;
        this.label2 = label2;
        this.label3 = label2;
    }

    public String getLabel1() {
        return label1.trim();
    }

    public void setLabel1(String label1) {
        this.label1 = label1.trim();
    }

    public String getLabel2() {
        return label2.trim();
    }

    public void setLabel2(String label2) {
        this.label2 = label2;
    }

    public String getLabel2frozen() {
        return label2frozen;
    }

    public void setLabel2frozen(String label2frozen) {
        this.label2frozen = label2frozen;
    }

    
    
    public void setUuid(String Uuid) {
        this.uuid = Uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public String getLabel3() {
        return label3.trim();
    }

    public void setLabel3(String label3) {
        System.out.println("author edited is set: " + label3);
        this.label3 = label3.trim();

        updateQuery = ControllerBean.ds.createQuery(PersistingEdit.class).field("reference").equal(controllerBean.getSearch().getFullnameWithComma());
        updateQuery.field("originalForm").equal(label2);
        updateQuery.field("editedForm").equal(label3);
        ops = ControllerBean.ds.createUpdateOperations(PersistingEdit.class).inc("counter", 1);
        ControllerBean.ds.update(updateQuery, ops, true);

        updateQueryCounter = ControllerBean.ds.createQuery(GlobalEditsCounter.class);
        opsCounter = ControllerBean.ds.createUpdateOperations(GlobalEditsCounter.class).inc("globalCounter", 2);
        ControllerBean.ds.update(updateQueryCounter, opsCounter, true);
        controllerBean.pushCounter();

        this.label2 = label3;

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
        return this.label1.compareTo(o.getLabel1());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.label1 != null ? this.label1.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MapLabels other = (MapLabels) obj;
        if ((this.label1 == null) ? (other.label1 != null) : !this.label1.equals(other.label1)) {
            return false;
        }
        return true;
    }
    
    
}
