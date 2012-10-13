package Model;

import BL.APIs.Mendeley.ContainerMendeleyDocuments;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
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

    public void setFullnameWithComma(String fullnameWithComma) {
        this.fullnameWithComma = fullnameWithComma;
    }

    public String getLd() {
        return ld.toArray().toString();
    }

    public void setLd(ArrayList<Quidam> ld) {
        this.ld = ld;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.fullnameWithComma != null ? this.fullnameWithComma.hashCode() : 0);
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
        if ((this.fullnameWithComma == null) ? (other.fullnameWithComma != null) : !this.fullnameWithComma.equals(other.fullnameWithComma)) {
            return false;
        }
        if (this.fullnameWithComma == null & other.fullnameWithComma == null) {
            if ((this.fullname == null) ? (other.fullname != null) : !this.fullname.equals(other.fullname)) {
                return false;
            }
            return true;
        }
        return true;
    }

    public String[] toArray() {
        String[] args = new String[2];
        args[0] = forename;
        args[1] = surname;
        return args;
    }

    public BasicDBObject toDBObject() {
        BasicDBObject doc = new BasicDBObject();

        doc.put("forename", forename);
        doc.put("surname", surname);

        return doc;
    }

    public static Quidam fromDBObject(DBObject doc) {
        Quidam quidam = new Quidam();

        quidam.forename = (String) doc.get("forename");
        quidam.surname = (String) doc.get("surname");

        return quidam;
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
