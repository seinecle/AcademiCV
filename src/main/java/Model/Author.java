/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import com.google.code.morphia.annotations.Entity;
import java.util.UUID;

/**
 *
 * @author C. Levallois
 */
@Entity
public class Author extends Quidam {


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

    public Author(String fullnameWithComma) {
        super(fullnameWithComma.trim());
    }
}
