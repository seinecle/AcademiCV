package BL.APIs.Mendeley;

import Model.Author;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.UUID;
import org.bson.types.ObjectId;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author C. Levallois
 */
@Entity
public class MendeleyDoc {

    @Id
    private ObjectId id;
    private String uuid;
    private String title;
    private String publication_outlet;
    private String year;
    private String mendeley_url;
    private String doi;
    @Embedded
    private ArrayList<Author> authors;

    public MendeleyDoc() {
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPublication_outlet(String publication_outlet) {
        this.publication_outlet = publication_outlet;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setMendeley_url(String mendeley_url) {
        this.mendeley_url = mendeley_url;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public void setAuthors(ArrayList<Author> authors) {
        this.authors = authors;
    }

    public String getTitle() {
        String stringReturned;
        if (title == null) {
            stringReturned = "title is null";
        } else {
            stringReturned = title;
        }
        return stringReturned;
    }

    public String getPublication_outlet() {
        return publication_outlet;
    }

    public String getYear() {
        return year;
    }

    public String getDoi() {
        return doi;
    }

    public ArrayList<Author> getAuthors() {

        return authors;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid.toString();
    }

    @Override
    public String toString() {
        String toPrint;
        StringBuilder sb;
        if (authors != null) {
            sb = new StringBuilder();
            for (Author coauthor : authors) {
//                System.out.println("author first name: " + author.getFirstName());
//                System.out.println("author last name: " + author.getLastName());
                if (coauthor.getForename() == null | coauthor.getSurname() == null) {
                    continue;
                }
                sb.append(coauthor.toString()).append(". ");
            }
            toPrint = "uuid: " + uuid + ". co-author(s): " + sb.toString();

        } else {
            toPrint = "uuid: " + uuid + ". No author listed";
        }
        return toPrint;
    }

    public BasicDBObject toDBObject() {

        BasicDBObject doc = new BasicDBObject();

        doc.put("publication_outlet", publication_outlet);
        doc.put("year", year);
        doc.put("mendeley_url", mendeley_url);
        doc.put("doi", doi);
        doc.put("title", title);
        System.out.println("title put in the db: " + title);

        doc.put("authors", "authors to be put in string");


        return doc;
    }

    public static MendeleyDoc fromDBObject(DBObject doc) {
        MendeleyDoc mendeleyDoc = new MendeleyDoc();

        mendeleyDoc.publication_outlet = (String) doc.get("publication_outlet");
        mendeleyDoc.title = (String) doc.get("title");
//        System.out.println("title of a doc retrieved from DB: " + (String) doc.get("title"));
        System.out.println("mendeley Title: " + mendeleyDoc.title);

        mendeleyDoc.year = (String) doc.get("year");
        System.out.println("year of a doc retrieved from DB: " + (String) doc.get("year"));

        mendeleyDoc.mendeley_url = (String) doc.get("mendeley_url");
        mendeleyDoc.doi = (String) doc.get("doi");
//        mendeleyDoc.authors = (String) doc.get("author");

        System.out.println("authors of a doc retrieved from DB: " + (String) doc.get("authors"));

        return mendeleyDoc;
    }
//    public String AuthorsToString(Author[] authors) {
//        String toPrint;
//        if (authors != null) {
//            StringBuilder sb = new StringBuilder();
//            for (Author coauthor : authors) {
////                System.out.println("author first name: " + author.getFirstName());
////                System.out.println("author last name: " + author.getLastName());
//                sb.append(coauthor.toStringForDB()).append("_");
//            }
//            toPrint = sb.toString();
//
//        } else {
//            toPrint = "No author listed";
//        }
//        return toPrint;
//
//    }
}
