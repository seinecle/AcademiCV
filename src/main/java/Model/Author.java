/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.util.Set;
import java.util.UUID;

/**
 *
 * @author C. Levallois
 */
public class Author extends Quidam {

    private int yearFirstCollab;
    private int yearLastCollab;
    private int timesMentioned;
    private Set<Author> setMostFrequentCoAuthors;

    public Author() {
    }

    public Author(String forename, String surname) {
        super(forename.trim(), surname.trim());
    }

    public Author(String forename, String surname, UUID uuid) {
        super(forename.trim(), surname.trim(), uuid);
    }

    public Author(String fullname, UUID uuid) {
        super(fullname.trim(), uuid);
    }

    public Author(String fullname, boolean isFullName) {
        super(fullname.trim(), isFullName);
    }

    public Author(String fullnameWithComma) {
        super(fullnameWithComma.trim());
    }

    public Author(String fullname, Affiliation affiliation, UUID uuid) {
        super(fullname,affiliation,uuid);
    }

    public int getYearFirstCollab() {
        return yearFirstCollab;
    }

    public void setYearFirstCollab(int yearFirstCollab) {
        this.yearFirstCollab = yearFirstCollab;
    }

    public int getYearLastCollab() {
        return yearLastCollab;
    }

    public void setYearLastCollab(int yearLastCollab) {
        this.yearLastCollab = yearLastCollab;
    }

    public int getTimesMentioned() {
        return timesMentioned;
    }

    public void setTimesMentioned(int timesMentioned) {
        this.timesMentioned = timesMentioned;
    }

    public Set<Author> getSetMostFrequentCoAuthors() {
        return setMostFrequentCoAuthors;
    }

    public void setSetMostFrequentCoAuthors(Set<Author> setMostFrequentCoAuthors) {
        this.setMostFrequentCoAuthors = setMostFrequentCoAuthors;
    }
    
    
    
}
