/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.APIs.Mendeley;

import Controller.ControllerBean;
import Model.Author;
import com.google.common.collect.TreeMultiset;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author C. Levallois
 */
public class AuthorInDocsCounter {

    static public TreeMultiset<Author> countAuthors() {

        List<MendeleyDoc> listDocs = ControllerBean.mendeleyDocs.getDocuments();
        TreeMultiset<Author> multisetAuthors = TreeMultiset.create();

        if (listDocs.isEmpty()) {
            System.out.println("list of MendeleyDocs is empty");
        }
        Iterator<MendeleyDoc> itContainer = listDocs.iterator();
        while (itContainer.hasNext()) {

            MendeleyDoc currDoc = itContainer.next();

            if (currDoc.getAuthors() != null) {
                List<Author> arrayAuthors = currDoc.getAuthors();
                for (Author coauthor : arrayAuthors) {
                    Author spellCheckedAuthor = ControllerBean.mapCloseMatches.get(coauthor);
//                    System.out.println("currAuthor is: " + coauthor.toStringForCircos());
//                    System.out.println("currAuthor spell checked is: " + spellCheckedAuthor.toStringForCircos());
                    multisetAuthors.add(spellCheckedAuthor);


                }
            }
        }
        return multisetAuthors;

    }
}
