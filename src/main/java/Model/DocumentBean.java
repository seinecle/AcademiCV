/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import BL.APIs.Mendeley.MendeleyDoc;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author C. Levallois
 */

@ManagedBean
@ViewScoped
public class DocumentBean implements Serializable {

    private List<MendeleyDoc> documents;
    private MendeleyDoc selectedDocument;

    public List<MendeleyDoc> getDocuments() {
        return documents;
    }

    public void setDocuments(List<MendeleyDoc> documents) {
        this.documents = documents;
    }

    public MendeleyDoc getSelectedDocument() {
        return selectedDocument;
    }

    public void setSelectedDocument(MendeleyDoc selectedDocument) {
        this.selectedDocument = selectedDocument;
    }

    
       
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<MendeleyDoc> containerIterator = documents.iterator();
        while (containerIterator.hasNext()) {
            MendeleyDoc currDoc = containerIterator.next();
            sb.append(currDoc.toString()).append("\n");
        }
        return sb.toString();
    }
}
