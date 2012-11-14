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
import java.util.TreeMap;
import java.util.TreeSet;
import javax.faces.bean.ManagedProperty;

/**
 *
 * @author C. Levallois
 */
public class ConvertToSegments {

    @ManagedProperty("#{controllerBean}")
    private ControllerBean controllerBean;

    public void setcontrollerBean(ControllerBean controllerBean) {
        this.controllerBean = controllerBean;
    }

    public ArrayList<Segment> convert() {
        Clock convertingToSegmentsClock = new Clock("converting to segments");
        ArrayList<Segment> al = new ArrayList();
        TreeMap<String, String> mapOfCorrectedNames = new TreeMap();
        TreeMultiset<String> ms = TreeMultiset.create();
        TreeSet<Author> setAuthors = new TreeSet();
        setAuthors.addAll(controllerBean.getSetAuthors());
        Author currAuthor;
        String spellCheckedAuthor;
        String currElement;

        //converts the set of MapLabels into a simpler map<String, String>
        Iterator<MapLabels> setMapLabelsIterator = controllerBean.getSetMapLabels().iterator();
        MapLabels currMapLabels;
        while (setMapLabelsIterator.hasNext()) {
            currMapLabels = setMapLabelsIterator.next();
            mapOfCorrectedNames.put(currMapLabels.getLabel1(), currMapLabels.getLabel2());
        }

        Iterator<Author> setAuthorsIterator = setAuthors.iterator();
        while (setAuthorsIterator.hasNext()) {


            currAuthor = setAuthorsIterator.next();
//            System.out.println("currAuthor in convertSegment: \"" + currAuthor.getFullnameWithComma() + "\"");
            spellCheckedAuthor = mapOfCorrectedNames.get(currAuthor.getFullnameWithComma());
//            System.out.println("spellCheckedAUthor in convertSegment: \"" + spellCheckedAuthor + "\"");


            try {
                ms.add(spellCheckedAuthor, currAuthor.getTimesMentioned());
//                System.out.println("this spellCheckedAuthor is added in x times: " + currAuthor.getTimesMentioned());

            } catch (NullPointerException e) {
//                        System.out.println("NPE caught in Convert To Segment");
            }
        }


        Iterator<String> msIterator = ms.elementSet().iterator();
        while (msIterator.hasNext()) {
            currElement = msIterator.next();
            al.add(new Segment(currElement, ms.count(currElement), false));
//            System.out.println("segment added to the list of segments");
        }

        convertingToSegmentsClock.closeAndPrintClock();
        return al;

    }
}
