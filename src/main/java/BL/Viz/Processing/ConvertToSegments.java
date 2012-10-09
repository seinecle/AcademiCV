/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.Viz.Processing;

import Controller.ControllerBean;
import Model.Author;
import Utils.Clock;
import com.google.common.collect.TreeMultiset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author C. Levallois
 */
public class ConvertToSegments {

    public ArrayList<Segment> convert(TreeMap<String, String> mapLabelsFromFinalCheck) {
        Clock convertingToSegmentsClock = new Clock("converting to segments");
        ArrayList<Segment> al = new ArrayList();
        TreeMap<String, String> mapAuthors = mapLabelsFromFinalCheck;
        TreeMultiset<String> ms = TreeMultiset.create();
        TreeSet<Author> authorsInMendeleyDocs = ControllerBean.authorsInMendeleyDocs;
        Author currAuthor;
        String spellCheckedAuthor;
        String currElement;

        Iterator<Author> setMendeleyAuthorsIterator = authorsInMendeleyDocs.iterator();
        while (setMendeleyAuthorsIterator.hasNext()) {

            currAuthor = setMendeleyAuthorsIterator.next();
            System.out.println("currAuthor in convertSegment: \"" + currAuthor.getFullnameWithComma() + "\"");
            spellCheckedAuthor = mapAuthors.get(currAuthor.getFullnameWithComma());
            System.out.println("spellCheckedAUthor in convertSegment: \"" + spellCheckedAuthor + "\"");


            try {
                ms.add(spellCheckedAuthor, currAuthor.getTimesMentioned());
                System.out.println("this spellCheckedAuthor is added in x times: " + currAuthor.getTimesMentioned());

            } catch (NullPointerException e) {
//                        System.out.println("NPE caught in Convert To Segment");
            }
        }


        Iterator<String> msIterator = ms.elementSet().iterator();
        while (msIterator.hasNext()) {
            currElement = msIterator.next();
            al.add(new Segment(currElement, ms.count(currElement), false, ControllerBean.uuid.toString()));
            System.out.println("segment added to the list of segments");
        }

        convertingToSegmentsClock.closeAndPrintClock();
        return al;

    }
}
