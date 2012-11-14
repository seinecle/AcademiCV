package BL.NameDisambiguation;

import Model.Author;
import Model.CloseMatchBean;
import Model.MapLabels;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class MapLabelsInitiator {

    Set<Author> setCloseMatches = new TreeSet();
    Set<Author> setAuthorsWithEdits = new TreeSet();
    TreeSet<MapLabels> setMapLabels = new TreeSet();
    ArrayList authorsInOneDoc;
    Author currAuth;
    String mainFirstName;
    String mainLastName;
    boolean debug;
    private boolean atLeastOneMatchFound = false;

    public MapLabelsInitiator() {
    }

    public TreeSet<MapLabels> check(Set<Author> setAuthors, Set<CloseMatchBean> setcmb) {



        Iterator<CloseMatchBean> setcmbIterator = setcmb.iterator();
        setCloseMatches = new HashSet();
        while (setcmbIterator.hasNext()) {
            CloseMatchBean closeMatchBean = setcmbIterator.next();
            setCloseMatches.add(new Author(closeMatchBean.getAuthor1()));
            setCloseMatches.add(new Author(closeMatchBean.getAuthor2()));
        }
        //***********
        // persist a map of labels
        // this is a map of the form ("original form of the author spelling in the set of Authors", "corrected form of this Author following user input")
        // this map is useful to keep a connection between original form an subsequent modifications
        // here, this map is populated with only the others that were not found to be similar to another.
        // this map will be modified in the paircheck and finalcheck webpages, with user input.
        //***********        

        Iterator<Author> setAuthorsIterator = setAuthors.iterator();
        Author currAuthor;

        while (setAuthorsIterator.hasNext()) {
            currAuthor = setAuthorsIterator.next();
            if (!setCloseMatches.contains(currAuthor)) {
                if (!currAuthor.getFullnameWithComma().trim().equals(mainFirstName + ", " + mainLastName)) {
//                    System.out.println("unambiguous author: " + currAuthor.getFullname());
//                    System.out.println("main Auth: " + mainFirstName + " " + mainLastName);

                    setMapLabels.add(new MapLabels(currAuthor.getFullnameWithComma(), currAuthor.getFullnameWithComma()));
                }
            }
        }
        return setMapLabels;

    }
}
