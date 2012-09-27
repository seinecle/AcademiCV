/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import org.bson.types.ObjectId;

/**
 *
 * @author C. Levallois
 */
@Entity
public class PersistingEdit {

    @Id
    private ObjectId id;
    
    private String reference;
    private String originalForm;
    private String editedForm;
    private int counter;
    
    public PersistingEdit(){
    }

    public PersistingEdit(String ref, String orig, String edited){
    
    this.reference = ref;
    this.originalForm = orig;
    this.editedForm = edited;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getOriginalForm() {
        return originalForm;
    }

    public void setOriginalForm(String originalForm) {
        this.originalForm = originalForm;
    }

    public String getEditedForm() {
        return editedForm;
    }

    public void setEditedForm(String editedForm) {
        this.editedForm = editedForm;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.reference != null ? this.reference.hashCode() : 0);
        hash = 73 * hash + (this.originalForm != null ? this.originalForm.hashCode() : 0);
        hash = 73 * hash + (this.editedForm != null ? this.editedForm.hashCode() : 0);
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
        final PersistingEdit other = (PersistingEdit) obj;
        if ((this.reference == null) ? (other.reference != null) : !this.reference.equals(other.reference)) {
            return false;
        }
        if ((this.originalForm == null) ? (other.originalForm != null) : !this.originalForm.equals(other.originalForm)) {
            return false;
        }
        if ((this.editedForm == null) ? (other.editedForm != null) : !this.editedForm.equals(other.editedForm)) {
            return false;
        }
        return true;
    }
    
    
    
}
