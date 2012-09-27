/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.Viz.Processing;

import Controller.ControllerBean;
import Model.Author;
import Model.MapLabels;
import Utils.Clock;
import com.google.common.collect.TreeMultiset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author C. Levallois
 */
public class ConvertToSegments {

    static public ArrayList<Segment> convert(List<MapLabels> listMapLabels) {
        Clock convertingToSegmentsClock = new Clock("converting to segments");
        ArrayList<Segment> al = new ArrayList();
        TreeMap<Author, Author> mapAuthors = new TreeMap();
        TreeMultiset<String> ms = TreeMultiset.create();
        List<Author> listMendeleyAuthors = ControllerBean.ds.find(Author.class).field("uuid").equal(ControllerBean.uuid.toString()).asList();
        Author currAuthor;
        Author spellCheckedAuthor;
        String currElement;
        for (MapLabels mapLabels : listMapLabels) {
            mapAuthors.put(new Author(mapLabels.getAuthor1displayed()), new Author(mapLabels.getAuthor2displayed()));
//            System.out.println("converting list to map");
        }

        Iterator<Author> listMendeleyAuthorsIterator = listMendeleyAuthors.iterator();
        while (listMendeleyAuthorsIterator.hasNext()) {

            currAuthor = listMendeleyAuthorsIterator.next();
            spellCheckedAuthor = mapAuthors.get(currAuthor);
            try {
                ms.add(spellCheckedAuthor.getFullnameWithComma());
//                        System.out.println("coauthor added in processing json: "+coauthor.getFullnameWithComma());

            } catch (NullPointerException e) {
//                        System.out.println("NPE caught in Convert To Segment");
            }
        }


        Iterator<String> msIterator = ms.elementSet().iterator();
        while (msIterator.hasNext()) {
            currElement = msIterator.next();
            al.add(new Segment(currElement, ms.count(currElement), false, ControllerBean.uuid.toString()));
//            System.out.println("segment added to the list of segments");
        }

        convertingToSegmentsClock.closeAndPrintClock();
        return al;

    }
}
