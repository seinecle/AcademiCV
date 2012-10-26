/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import java.util.HashSet;
import org.bson.types.ObjectId;

/**
 *
 * @author C. Levallois
 */
@Entity
public class PersistingAcademic {

    @Id
    private ObjectId id;
    private String fullNameWithComma;
    private int birthYear;
    private int countDocuments;
    private int countCoAuthors;
    private int countMediaMentions;
    private int discipline1;
    private int discipline2;
    private int discipline3;
    private int searchCount;
    private HashSet<Affiliation> setAffiliations;

    public PersistingAcademic() {
    }

    public String getFullNameWithComma() {
        return fullNameWithComma;
    }

    public void setFullNameWithComma(String fullNameWithComma) {
        this.fullNameWithComma = fullNameWithComma;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public int getCountDocuments() {
        return countDocuments;
    }

    public void setCountDocuments(int countDocuments) {
        this.countDocuments = countDocuments;
    }

    public int getCountCoAuthors() {
        return countCoAuthors;
    }

    public void setCountCoAuthors(int countCoAuthors) {
        this.countCoAuthors = countCoAuthors;
    }

    public int getCountMediaMentions() {
        return countMediaMentions;
    }

    public void setCountMediaMentions(int countMediaMentions) {
        this.countMediaMentions = countMediaMentions;
    }

    public int getDiscipline1() {
        return discipline1;
    }

    public void setDiscipline1(int discipline1) {
        this.discipline1 = discipline1;
    }

    public int getDiscipline2() {
        return discipline2;
    }

    public void setDiscipline2(int discipline2) {
        this.discipline2 = discipline2;
    }

    public int getDiscipline3() {
        return discipline3;
    }

    public void setDiscipline3(int discipline3) {
        this.discipline3 = discipline3;
    }

    public int getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(int searchCount) {
        this.searchCount = searchCount;
    }

    public HashSet<Affiliation> getSetAffiliations() {
        return setAffiliations;
    }

    public void setSetAffiliations(HashSet<Affiliation> setAffiliations) {
        this.setAffiliations = setAffiliations;
    }
    
    
}
