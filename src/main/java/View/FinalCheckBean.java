/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import BL.Viz.Processing.Segment;
import Controller.ControllerBean;
import Model.GlobalEditsCounter;
import Model.MapLabels;
import Model.PersistingEdit;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author C. Levallois
 */
@ManagedBean
@ViewScoped
public class FinalCheckBean implements Serializable {

    private ArrayList<Segment> segments;
    private List<MapLabels> listMapLabels;
    private TreeSet<String> setCheckedLabels;
    private HashSet<MapLabels> setLabelsToBeReinjected = new HashSet();
    private HashMap<String, String> bufferMap = new HashMap();
    private List<MapLabels> listCheckedLabels;
    private Query<PersistingEdit> updateQuery;
    private UpdateOperations<PersistingEdit> ops;
    private Query<GlobalEditsCounter> updateQueryCounter;
    private UpdateOperations<GlobalEditsCounter> opsCounter;
    private TreeMap<String, String> mappingEditedLabels = new TreeMap();
    private TreeMap<String, String> mappingLabelsForSegments = new TreeMap();

    public FinalCheckBean() {
        System.out.println("new FinalCheckBean!");

        //deleting segments and labels that could be in storage due to the user landing on this page with the back button.
        Query q3 = ControllerBean.ds.createQuery(Segment.class).field("uuid").equal(ControllerBean.uuid.toString());
        ControllerBean.ds.delete(q3);

        setCheckedLabels = new TreeSet();
        listCheckedLabels = new ArrayList();

        //This procedure removes duplicates that were created during the solving of ambiguities in the previous page
        //(like, two labels "Alberto Einstein" and "Alberta Einstein" have been edited to "Albert Einstein" => we want only one of them in the finalcheck page.0
        //however, we want to keep trace of all Alberto and Albertas because in the final report we need to count how all of them merged into Albert Einstein
        //this is the reason for the "setLabelsToBeReinjected", which plays a role in the "moveon" function
        //I use "label2frozen" to keep a trace of the label2 even if it will be edited by the user (into label2 and label3, see the MapLabels class)
        MapLabels currMapLabels;
        for (MapLabels mapLabels : ControllerBean.setMapLabels) {
            System.out.println("currMapLabel is: ");
            System.out.println(mapLabels.getLabel1() + ", " + mapLabels.getLabel2());
            if (setCheckedLabels.add(mapLabels.getLabel2())) {
                System.out.println("currMapLabel included in the final check page: ");
                System.out.println(mapLabels.getLabel1() + ", " + mapLabels.getLabel2());
                currMapLabels = new MapLabels(mapLabels.getLabel1(), mapLabels.getLabel2());
                currMapLabels.setLabel2frozen(currMapLabels.getLabel2());
                listCheckedLabels.add(currMapLabels);
                Collections.sort(listCheckedLabels);
//                System.out.println("mapLabel in list: " +mapLabels.getAuthor2().getFullname());
            } else {
                System.out.println("mapLabel temporarily ignored:");
                System.out.println(mapLabels.getLabel1() + ", " + mapLabels.getLabel2());
                setLabelsToBeReinjected.add(mapLabels);
            }
        }
    }

    public List<MapLabels> getListCheckedLabels() {
        return listCheckedLabels;
    }

    public void deleteRow() {
        Iterator<MapLabels> listCheckedLabelsIterator = listCheckedLabels.iterator();
        MapLabels currMapLabels;
        while (listCheckedLabelsIterator.hasNext()) {
            currMapLabels = listCheckedLabelsIterator.next();
            currMapLabels.setEditable(false);
            if (currMapLabels.isDeleted()) {
                //persisting this edit permanently
                updateQuery = ControllerBean.ds.createQuery(PersistingEdit.class).field("reference").equal(ControllerBean.getSearch().getFullnameWithComma());
                updateQuery.field("originalForm").equal(currMapLabels.getLabel2());
                updateQuery.field("editedForm").equal("deleted");
                ops = ControllerBean.ds.createUpdateOperations(PersistingEdit.class).inc("counter", 1);
                ControllerBean.ds.update(updateQuery, ops, true);

                updateQueryCounter = ControllerBean.ds.createQuery(GlobalEditsCounter.class);
                opsCounter = ControllerBean.ds.createUpdateOperations(GlobalEditsCounter.class).inc("globalCounter", 1);
                ControllerBean.ds.update(updateQueryCounter, opsCounter, true);
                ControllerBean.pushCounter();


                listCheckedLabelsIterator.remove();
            }

        }
    }

    public void saveedits() {
        //get all existing value but set "editable" to false 
        for (MapLabels mapLabels : listCheckedLabels) {
            mapLabels.setEditable(false);
        }
    }

    public String moveon() {
        System.out.println("next button clicked, should move to the report page");

        //puts all the cases presented to the user in the final check page into a set
        for (MapLabels element : listCheckedLabels) {
            ControllerBean.setMapLabels.add(new MapLabels(element.getLabel1(), element.getLabel2()));
        }


        //adds all the cases not peesented to the user (because duplicates), but still relevant if we want to count how many original authors there were
        for (MapLabels element : listCheckedLabels) {
            bufferMap.put(element.getLabel2frozen(), element.getLabel3());
        }


        String currLabel1;
        String currLabel2;
        for (MapLabels element : setLabelsToBeReinjected) {
            currLabel1 = element.getLabel1();
            System.out.println("currLabel1: " + currLabel1);
            currLabel2 = bufferMap.get(element.getLabel2());
            System.out.println("currLabel2: " + currLabel2);

            ControllerBean.setMapLabels.add(new MapLabels(currLabel1, currLabel2));
        }

        //launch of the methods to compute metrics useful to the final report
        ControllerBean.computationsBeforeReport();
//
//        ControllerBean.transformToJson(segments);
        return "report?faces-redirect=true";
    }
}
