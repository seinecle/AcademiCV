/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.Viz.Processing;

import Model.Author;
import Utils.Clock;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author C. Levallois
 */
public class ConvertToSegments {

    public ArrayList<Segment> convert(Set<Author> setAuthorsFromController) {
        Clock convertingToSegmentsClock = new Clock("converting to segments");
        ArrayList<Segment> segmentList = new ArrayList();
        Set<Author> setAuthors = new TreeSet();
        setAuthors.addAll(setAuthorsFromController);
        Author currAuthor;

        Iterator<Author> setAuthorsIterator = setAuthors.iterator();
        while (setAuthorsIterator.hasNext()) {
            currAuthor = setAuthorsIterator.next();
            try {
                segmentList.add(new Segment(currAuthor.getFullname(), currAuthor.getTimesMentioned(), false));
            } catch (NullPointerException e) {
                System.out.println("oops one author had no getTimesMentioned propertyM "+currAuthor.getFullname());
            }
        }
        convertingToSegmentsClock.closeAndPrintClock();
        return segmentList;

    }
}
