/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.APIs.Mendeley;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author C. Levallois
 */
public class MendeleyDocument implements Serializable{

    private String uuid;
    private String title;
    private String publication_outlet;
    private String mendeley_url;
    private String doi;
    private String year;
    private List<author> authors = new ArrayList();

    MendeleyDocument() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublication_outlet() {
        return publication_outlet;
    }

    public void setPublication_outlet(String publication_outlet) {
        this.publication_outlet = publication_outlet;
    }

    public String getMendeley_url() {
        return mendeley_url;
    }

    public void setMendeley_url(String mendeley_url) {
        this.mendeley_url = mendeley_url;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<author> authors) {
        this.authors = authors;
    }

    static class author {

        String forename;
        String surname;

        author() {
        }

        public String getForename() {
            return forename;
        }

        public void setForename(String forename) {
            this.forename = forename;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }
    }
}
