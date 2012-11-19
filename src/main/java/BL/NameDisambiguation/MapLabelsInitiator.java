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

    Set<String> setCloseMatches = new TreeSet();
    Set<Author> setAuthorsWithEdits = new TreeSet();
    TreeSet<MapLabels> setMapLabels = new TreeSet();
    ArrayList authorsInOneDoc;
    Author currAuth;
    boolean debug;

    public MapLabelsInitiator() {
    }

    public TreeSet<MapLabels> check(Set<Author> setAuthors, Set<CloseMatchBean> setcmb, Author search) {



        Iterator<CloseMatchBean> setcmbIterator = setcmb.iterator();
        setCloseMatches = new HashSet();
        while (setcmbIterator.hasNext()) {
            CloseMatchBean closeMatchBean = setcmbIterator.next();
            setCloseMatches.add(closeMatchBean.getAuthor1());
            setCloseMatches.add(closeMatchBean.getAuthor2());
        }
        System.out.println("size of setcmb: " + setcmb.size());
        //***********
        // persist a map of labels
        // this is a map of the form ("original form of the author spelling in the set of Authors", "corrected form of this Author following user input")
        // this map is useful to keep a connection between original form an subsequent modifications
        // here, this map is populated with only the others that were not found to be similar to another.
        // this map will be modified in the paircheck and finalcheck webpages, by user input.
        //***********        

        Iterator<Author> setAuthorsIterator = setAuthors.iterator();
        Author currAuthor;
        String currAuthorFullNameWithComma;
        while (setAuthorsIterator.hasNext()) {
            currAuthor = setAuthorsIterator.next();
            currAuthorFullNameWithComma = currAuthor.getFullnameWithComma();
            if (!setCloseMatches.contains(currAuthorFullNameWithComma)) {
                if (!currAuthorFullNameWithComma.equals(search.getFullnameWithComma())) {
//                    System.out.println("unambiguous author: " + currAuthor.getFullname());
//                    System.out.println("main Auth: " + mainFirstName + " " + mainLastName);

                    setMapLabels.add(new MapLabels(currAuthorFullNameWithComma, currAuthorFullNameWithComma));
                }
            }
        }
        return setMapLabels;

    }
}