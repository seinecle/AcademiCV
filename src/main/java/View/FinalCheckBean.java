/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import BL.Viz.Processing.ConvertToSegments;
import BL.Viz.Processing.Segment;
import Controller.ControllerBean;
import Model.GlobalEditsCounter;
import Model.MapLabels;
import Model.PersistingEdit;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
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
    private List<MapLabels> listCheckedLabels;
    private Query<PersistingEdit> updateQuery;
    private UpdateOperations<PersistingEdit> ops;
    private Query<GlobalEditsCounter> updateQueryCounter;
    private UpdateOperations<GlobalEditsCounter> opsCounter;
    private TreeMap<String, String> mappingEditedLabels = new TreeMap();
    private TreeMap<String, String> mappingLabelsForSegments = new TreeMap();

    public FinalCheckBean() {
        System.out.println("new FinalCheckBean!");
        Query q3 = ControllerBean.ds.createQuery(Segment.class).field("uuid").equal(ControllerBean.uuid.toString());
        ControllerBean.ds.delete(q3);

        listMapLabels = ControllerBean.ds.find(MapLabels.class).field("uuid").equal(ControllerBean.uuid.toString()).asList();
        setCheckedLabels = new TreeSet();
        listCheckedLabels = new ArrayList();

        //This procedure removes duplicates that were created during the solving of ambiguities in the previous page
        for (MapLabels mapLabels : listMapLabels) {
            if (setCheckedLabels.add(mapLabels.getLabel2())) {
//                MapLabels newML = new MapLabels(mapLabels.getAuthor1().getFullname(), mapLabels.getAuthor2().getFullname());
//                listCheckedLabels.add(newML);
                listCheckedLabels.add(new MapLabels(mapLabels.getLabel2(), mapLabels.getLabel2()));
                Collections.sort(listCheckedLabels);
//                System.out.println("mapLabel in list: " +mapLabels.getAuthor2().getFullname());
            }
        }
    }

    public ArrayList<Segment> close() {

        //converts the list of edited labels into a map
        for (MapLabels element : listCheckedLabels) {
            mappingEditedLabels.put(element.getLabel1(), element.getLabel2());
        }

        //converts the original list of labels into a map, substituting the edited label dating from the pairc check by the one found in the final check

        for (MapLabels element : listMapLabels) {
            mappingLabelsForSegments.put(element.getLabel1(), mappingEditedLabels.get(element.getLabel2()));
        }

        //PERSIST SEGMENTS
        segments = ConvertToSegments.convert(mappingLabelsForSegments);
//        System.out.println("segments size: "+segments.size());
//        for (Segment segment : segments) {
//            ControllerBean.ds.save(segment);
////            System.out.println("segment persisted");
//        }
        return segments;

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
        for (MapLabels element : listCheckedLabels) {
            mappingEditedLabels.put(element.getLabel1(), element.getLabel2());
        }

        //converts the original list of labels into a map, substituting the edited label dating from the pairc check by the one found in the final check
        for (MapLabels element : listMapLabels) {
            mappingLabelsForSegments.put(element.getLabel1(), mappingEditedLabels.get(element.getLabel2()));
        }

        //PERSIST SEGMENTS
        segments = ConvertToSegments.convert(mappingLabelsForSegments);

        segments.add(new Segment(ControllerBean.getSearch().getFullnameWithComma(), 1, true));
        ControllerBean.setJson(new Gson().toJson(segments));
//
//        ControllerBean.transformToJson(segments);
        return "report?faces-redirect=true";
    }
}
