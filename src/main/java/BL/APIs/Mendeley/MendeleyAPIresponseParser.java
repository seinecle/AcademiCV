package BL.APIs.Mendeley;

import BL.APIs.Mendeley.MendeleyDocument.author;
import Model.Author;
import Model.Document;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author C. Levallois
 */
public class MendeleyAPIresponseParser {

    ContainerMendeleyDocuments container;
    private int nbMendeleyDocs = 0;

    public MendeleyAPIresponseParser(ContainerMendeleyDocuments container, Author search) {
        this.container = container;
    }

    public Set<Document> parse() {
        List<MendeleyDocument> currList;
        Set<Document> setMendeleyDocs = new HashSet();

        // this if else deals with the case when the container of Mendeley docs is empty, bc the Mendeley API was unresponsive
        // if the container is null, just create an empty list
        if (container != null) {
            currList = container.getDocuments();
        } else {
            currList = new ArrayList();

        }
        Iterator<MendeleyDocument> currListIterator = currList.iterator();
        Iterator<author> currDocAuthorsIterator;
        Document newDoc;
        MendeleyDocument currDoc;
        HashSet<Author> setcurrAuthors;
        while (currListIterator.hasNext()) {
            currDoc = currListIterator.next();

            //this skips the documents that have incomplete data. They will simply not be considered by the application.
            if (currDoc.getTitle() == null || currDoc.getAuthors() == null || currDoc.getYear() == null) {
                continue;
            }
            newDoc = new Document();
            currDocAuthorsIterator = currDoc.getAuthors().iterator();
            setcurrAuthors = new HashSet();
            author currauthor;

            while (currDocAuthorsIterator.hasNext()) {
                currauthor = currDocAuthorsIterator.next();
                if ("".equals(currauthor.forename.trim()) || "".equals(currauthor.surname.trim())) {
                    continue;
                }
                setcurrAuthors.add(new Author(currauthor.forename, currauthor.surname));
//                System.out.println("currauthor.forename in Mendeley parser: " + currauthor.getForename());
//                System.out.println("currauthor.surname in Mendeley parser: " + currauthor.getSurname());
            }
            newDoc.setAuthors(setcurrAuthors);

            newDoc.setDoi(currDoc.getDoi());
            newDoc.setTitle(currDoc.getTitle());
            newDoc.setMendeley_url(currDoc.getMendeley_url());
            newDoc.setPublication_outlet(currDoc.getPublication_outlet());
            newDoc.setWhereFrom("mendeley");
            newDoc.setYear(Integer.parseInt(currDoc.getYear()));
            setMendeleyDocs.add(newDoc);
        }

        return setMendeleyDocs;
    }
}
