package Model;

import Controller.ControllerBean;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author C. Levallois
 */
public class Document implements Comparable<Document>, Serializable {

    private Integer docId;
    private String title;
    private String publication_outlet;
    private String summary;
    private Integer year;
    private String whereFrom;
    private String mendeley_url;
    private String nyt_url;
    private String doi;
    private DateTime creationDateTime;
    private String topicArxiv;
    private HashSet<Author> authors;
    private String typeOutlet;
    private String language;

    public Document() {
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPublication_outlet(String publication_outlet) {
        this.publication_outlet = publication_outlet;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public void setMendeley_url(String mendeley_url) {
        this.mendeley_url = mendeley_url;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public void setAuthors(HashSet<Author> authors) {
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
        if (publication_outlet == null) {
            return "[publisher not identified]";
        } else {
            return publication_outlet;

        }
    }

    public Integer getYear() {
        return year;
    }

    public String getDoi() {
        return doi;
    }

    public HashSet<Author> getAuthors() {
        return authors;
    }

    public String getAuthorsToString() {
        StringBuilder sb = new StringBuilder();
        if (!authors.isEmpty()) {
            sb.append("with ");
        }
        Iterator<Author> authorsIterator = authors.iterator();
        while (authorsIterator.hasNext()) {
            Author author = authorsIterator.next();
            sb.append(author.getFullname());
            sb.append(", ");
        }
        return StringUtils.removeEnd(sb.toString(), ", ");
    }

    public String getWhereFrom() {
        return whereFrom;
    }

    public void setWhereFrom(String whereFrom) {
        this.whereFrom = whereFrom;
    }

    public DateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(DateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public String getTopicArxiv() {
        return topicArxiv;
    }

    public void setTopicArxiv(String topicArxiv) {
        this.topicArxiv = topicArxiv;
    }

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public String getTypeOutlet() {
        return typeOutlet;
    }

    public void setTypeOutlet(String typeOutlet) {
        this.typeOutlet = typeOutlet;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getNyt_url() {
        return nyt_url;
    }

    public void setNyt_url(String nyt_url) {
        this.nyt_url = nyt_url;
    }

    @Override
    public String toString() {
        String toPrint;
        StringBuilder sb;
        if (authors != null) {
            sb = new StringBuilder();
            Author coauthor;
            Iterator<Author> setAuthorsIterator = authors.iterator();
            while (setAuthorsIterator.hasNext()) {
                coauthor = setAuthorsIterator.next();
//                System.out.println("author first name: " + author.getFirstName());
//                System.out.println("author last name: " + author.getLastName());
//                if (coauthor.getForename() == null | coauthor.getSurname() == null) {
//                    continue;
//                }
                sb.append(coauthor.getFullname()).append(". ");
            }
            toPrint = "co-author(s): " + sb.toString() + " Title: " + title + ". Year: " + year + ".\n";

        } else {
            toPrint = "No author listed. Title: " + title + ". Year: " + year + ".\n";
        }
        return toPrint;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 19 * hash + (this.publication_outlet != null ? this.publication_outlet.hashCode() : 0);
        hash = 19 * hash + (this.whereFrom != null ? this.whereFrom.hashCode() : 0);
        hash = 19 * hash + (this.year != null ? this.year.hashCode() : 0);
        hash = 19 * hash + (this.authors != null ? this.authors.hashCode() : 0);
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
        final Document other = (Document) obj;
        if ((this.title == null) ? (other.title != null) : !this.title.equals(other.title)) {
            return false;
        }
        if ((this.publication_outlet == null) ? (other.publication_outlet != null) : !this.publication_outlet.equals(other.publication_outlet)) {
            return false;
        }
        if (this.year != other.year && (this.year == null || !this.year.equals(other.year))) {
            return false;
        }
        if (this.authors != other.authors && (this.authors == null || !this.authors.equals(other.authors))) {
            return false;
        }
        if (this.whereFrom != other.whereFrom && (this.whereFrom == null || !this.whereFrom.equals(other.whereFrom))) {
            return false;
        }
        return true;
    }

//    @Override
//    public int compareTo(Document other) {
//        int result;
//        result = getYear().compareTo(other.getYear());
//        return -result;
//    }
    @Override
    public int compareTo(Document other) {
        int result;
        result = getTitle().compareTo(other.getTitle());
        return result;
    }
}
