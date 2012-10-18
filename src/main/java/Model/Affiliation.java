/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author C. Levallois
 */
public class Affiliation {

    private String organizationLevel1;
    private String organizationLevel2;
    private String organizationLevel3;

    public Affiliation() {
    }

    public Affiliation(String organizationLevel1) {
        this.organizationLevel1 = organizationLevel1;
    }

    public Affiliation(String organizationLevel1, String organizationLevel2) {
        this.organizationLevel1 = organizationLevel1;
        this.organizationLevel2 = organizationLevel2;
    }

    public Affiliation(String organizationLevel1, String organizationLevel2, String organizationLevel3) {
        this.organizationLevel1 = organizationLevel1;
        this.organizationLevel2 = organizationLevel2;
        this.organizationLevel3 = organizationLevel3;
    }

    public String getOrganizationLevel1() {
        return organizationLevel1;
    }

    public void setOrganizationLevel1(String organizationLevel1) {
        this.organizationLevel1 = organizationLevel1;
    }

    public String getOrganizationLevel2() {
        return organizationLevel2;
    }

    public void setOrganizationLevel2(String organizationLevel2) {
        this.organizationLevel2 = organizationLevel2;
    }

    public String getOrganizationLevel3() {
        return organizationLevel3;
    }

    public void setOrganizationLevel3(String organizationLevel3) {
        this.organizationLevel3 = organizationLevel3;
    }
    
    
}
