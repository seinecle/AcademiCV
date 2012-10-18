package Model;

import BL.APIs.Mendeley.ContainerMendeleyDocuments;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import org.bson.types.ObjectId;

//@Embedded
@Entity
public class Quidam implements Comparable<Quidam>, Serializable {

    @Id
    ObjectId id;
    private String forename;
    private String surname;
    private String fullname;
    private String fullnameWithComma;
    private ArrayList<Quidam> ld;
    private ContainerMendeleyDocuments container;
    private String uuid;
    private HashSet<Affiliation> setAffiliations = new HashSet();

    public Quidam() {
    }

    public Quidam(String forename, String surname) {
        this.forename = forename.trim();
        this.surname = surname.trim();
    }

    public Quidam(String forename, String surname, UUID uuid) {
        this.forename = forename.trim();
        this.surname = surname.trim();
        this.fullname = forename.trim() + " " + surname.trim();
        this.uuid = uuid.toString();
    }

    public Quidam(String fullname, UUID uuid) {
        this.fullname = fullname.trim();
        this.uuid = uuid.toString();
    }

    public Quidam(String fullname, Affiliation affiliation, UUID uuid) {
        this.fullname = fullname.trim();
        this.uuid = uuid.toString();
        this.setAffiliations.add(affiliation);
    }

    public Quidam(String fullnameWithComma) {
        this.fullnameWithComma = fullnameWithComma.trim();
    }

    public void setForename(String forename) {
        this.forename = forename.trim();
    }

    public String getForename() {
        return forename;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname.trim();
    }

    public String getFullname() {

        String fullnameToReturn;
        if (fullname == null) {
            fullnameToReturn = forename + " " + surname;
        } else {
            fullnameToReturn = this.fullname.trim();
        }
        return fullnameToReturn;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname.trim();
    }

    public String getFullnameWithComma() {

        String fullnameWithCommaToReturn;
        if (fullnameWithComma == null) {
            fullnameWithCommaToReturn = surname + ", " + forename;
        } else {
            fullnameWithCommaToReturn = this.fullnameWithComma.trim();
        }
        return fullnameWithCommaToReturn;
    }

    public void setFullnameWithComma(String newFullnameWithComma) {
        String[] arrayTerms = newFullnameWithComma.split(",");
        this.fullnameWithComma = newFullnameWithComma;
        this.forename = arrayTerms[1].trim();
        this.surname = arrayTerms[0].trim();
        this.fullname = arrayTerms[1].trim() + " " + arrayTerms[0].trim();
    }

    public String getLd() {
        return ld.toArray().toString();
    }

    public void setLd(ArrayList<Quidam> ld) {
        this.ld = ld;
    }

    public HashSet<Affiliation> getSetAffiliations() {
        return setAffiliations;
    }

    public void setSetAffiliations(HashSet<Affiliation> setAffiliations) {
        this.setAffiliations = setAffiliations;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.forename != null ? this.forename.hashCode() : 0);
        hash = 53 * hash + (this.surname != null ? this.surname.hashCode() : 0);
        hash = 53 * hash + (this.fullname != null ? this.fullname.hashCode() : 0);
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
        final Quidam other = (Quidam) obj;
        if ((this.forename == null) ? (other.forename != null) : !this.forename.equals(other.forename)) {
            return false;
        }
        if ((this.surname == null) ? (other.surname != null) : !this.surname.equals(other.surname)) {
            return false;
        }
        if ((this.fullname == null) ? (other.fullname != null) : !this.fullname.equals(other.fullname)) {
            return false;
        }
        return true;
    }



    public String[] toArray() {
        String[] args = new String[2];
        args[0] = forename;
        args[1] = surname;
        return args;
    }

    @Override
    public int compareTo(Quidam other) {
        int result;
        if (getFullnameWithComma() == null || other.getFullnameWithComma() == null) {
            result = (getForename() + getSurname()).compareTo(other.getForename() + other.getSurname());
        } else {
            result = getFullnameWithComma().compareTo(other.getFullnameWithComma());
        }
        return result;
    }

    public ContainerMendeleyDocuments getContainer() {
        return container;
    }

    public void setContainer(ContainerMendeleyDocuments container) {
        this.container = container;
    }
}
