/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.DocumentHandling;

import Controller.ControllerBean;
import Model.Author;
import Model.Document;
import com.google.common.collect.HashMultiset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.faces.bean.ManagedProperty;

/**
 *
 * @author C. Levallois
 */
public class AuthorsExtractor {

    @ManagedProperty("#{controllerBean}")
    private ControllerBean controllerBean;

    public void setcontrollerBean(ControllerBean controllerBean) {
        this.controllerBean = controllerBean;
    }

    public AuthorsExtractor() {
    }

    public HashSet<Author> extractFromOneDoc(Document doc) {
        return doc.getAuthors();
    }

    public HashMultiset<Author> extractFromSetDocs(Set<Document> setDocs) {

        HashMultiset<Author> setAllAuthors = HashMultiset.create();
        Iterator<Document> setDocsIterator = setDocs.iterator();
        while (setDocsIterator.hasNext()) {
            setAllAuthors.addAll(setDocsIterator.next().getAuthors());
        }
        return setAllAuthors;
    }

    public void extractCurrSearchedAuthor() {
        Iterator<Author> setAuthorsIterator = controllerBean.getSetAuthors().iterator();
        Author currAuthor;
        Author mergedAuthor;
        while (setAuthorsIterator.hasNext()) {
            currAuthor = setAuthorsIterator.next();
            if (currAuthor.getFullname().equals(controllerBean.getSearch().getFullname())) {
                mergedAuthor = new AuthorMerger().mergeAuthors(currAuthor, controllerBean.getSearch());
                controllerBean.setSearch(mergedAuthor);
            }
        }
    }
}
