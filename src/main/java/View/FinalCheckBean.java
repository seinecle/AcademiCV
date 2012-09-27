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
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

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

    public FinalCheckBean() {
    }

    @PostConstruct
    public void init() {
        Query q3 = ControllerBean.ds.createQuery(Segment.class).field("uuid").equal(ControllerBean.uuid.toString());
        ControllerBean.ds.delete(q3);

        listMapLabels = ControllerBean.ds.find(MapLabels.class).field("uuid").equal(ControllerBean.uuid.toString()).asList();
        setCheckedLabels = new TreeSet();
        listCheckedLabels = new ArrayList();

        //This procedure removes duplicates that were created during the solving of ambiguities in the previous page
        for (MapLabels mapLabels : listMapLabels) {
            if (setCheckedLabels.add(mapLabels.getAuthor2().getFullnameWithComma())) {
//                MapLabels newML = new MapLabels(mapLabels.getAuthor1().getFullname(), mapLabels.getAuthor2().getFullname());
//                listCheckedLabels.add(newML);
                listCheckedLabels.add(mapLabels);
                Collections.sort(listCheckedLabels);
//                System.out.println("mapLabel in list: " +mapLabels.getAuthor2().getFullname());
            }
        }
    }

    @PreDestroy
    public void close() {


        //PERSIST SEGMENTS
        segments = ConvertToSegments.convert(listCheckedLabels);
//        System.out.println("segments size: "+segments.size());
        for (Segment segment : segments) {
            ControllerBean.ds.save(segment);
//            System.out.println("segment persisted");
        }


    }

    public List<MapLabels> getListCheckedLabels() {
        return listCheckedLabels;
    }

    public String deleteRow() {
        Iterator<MapLabels> listCheckedLabelsIterator = listCheckedLabels.iterator();
        MapLabels currMapLabels;
        while (listCheckedLabelsIterator.hasNext()) {
            currMapLabels = listCheckedLabelsIterator.next();
            currMapLabels.setEditable(false);
            if (currMapLabels.isDeleted()) {
                //persisting this edit permanently
                updateQuery = ControllerBean.ds.createQuery(PersistingEdit.class).field("reference").equal(ControllerBean.getSearch().getFullnameWithComma());
                updateQuery.field("originalForm").equal(currMapLabels.getAuthor2().getFullnameWithComma());
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
        return null;
    }

    public void saveedits() {
        //get all existing value but set "editable" to false 
        for (MapLabels mapLabels : listCheckedLabels) {
            mapLabels.setEditable(false);
        }
    }

    public void moveon() throws IOException {
        close();
        ControllerBean.transformToJson();
        FacesContext.getCurrentInstance().getExternalContext().redirect("processing.xhtml");
    }
}
