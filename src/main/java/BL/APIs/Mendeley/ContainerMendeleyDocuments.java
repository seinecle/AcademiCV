/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.APIs.Mendeley;

import java.io.Serializable;
import java.util.ArrayList;
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
public class ContainerMendeleyDocuments implements Serializable {

    private List<MendeleyDocument> documents = new ArrayList();

    public List<MendeleyDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<MendeleyDocument> documents) {
        this.documents = documents;
    }

       
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<MendeleyDocument> containerIterator = documents.iterator();
        while (containerIterator.hasNext()) {
            MendeleyDocument currDoc = containerIterator.next();
            sb.append(currDoc.toString()).append("\n");
        }
        return sb.toString();
    }
}
