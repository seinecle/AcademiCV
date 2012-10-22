/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.APIs.NYT;

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
public class ContainerNYTDocuments implements Serializable {

    private List<NYTDoc> results;

    ContainerNYTDocuments() {
        results = new ArrayList();
    }

    public List<NYTDoc> getDocuments() {
        return results;
    }

    public void setDocuments(List<NYTDoc> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<NYTDoc> containerIterator = results.iterator();
        while (containerIterator.hasNext()) {
            NYTDoc currDoc = containerIterator.next();
            sb.append(currDoc.toString()).append("\n");
        }
        return sb.toString();
    }
}
