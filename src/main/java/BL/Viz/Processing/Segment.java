/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.Viz.Processing;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import java.io.Serializable;
import org.bson.types.ObjectId;

/**
 *
 * @author C. Levallois
 */
@Entity
public class Segment implements Serializable {

    @Id
    private ObjectId id;
    private String label;
    private int count;
    private boolean isMain;
    private String uuid;

    public Segment() {
    }

    public Segment(String label, int count, boolean isMain) {
        this.label = label;
        this.count = count;
        this.isMain = isMain;
    }

    public Segment(String label, int count, boolean isMain, String uuid) {
        this.label = label;
        this.count = count;
        this.isMain = isMain;
        this.uuid = uuid;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isIsMain() {
        return isMain;
    }

    public void setIsMain(boolean isMain) {
        this.isMain = isMain;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.label != null ? this.label.hashCode() : 0);
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
        final Segment other = (Segment) obj;
        if ((this.label == null) ? (other.label != null) : !this.label.equals(other.label)) {
            return false;
        }
        return true;
    }
}
