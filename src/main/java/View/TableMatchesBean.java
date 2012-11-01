/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Controller.ControllerBean;
import Model.CloseMatchBean;
import Model.GlobalEditsCounter;
import Model.MapLabels;
import Model.PersistingEdit;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.push.PushContext;

/**
 *
 * @author C. Levallois
 */
@ManagedBean
@ViewScoped
public class TableMatchesBean implements Serializable {

    private List<CloseMatchBean> listCloseMatchesDisplayed = new ArrayList();
    private TreeSet<CloseMatchBean> setClosematchesOriginal = new TreeSet();
    private String mergedAuthor;
    private Integer optionChosen;
    private boolean renderInputMerge;
    private Query<PersistingEdit> updateQuery;
    private UpdateOperations<PersistingEdit> ops;
    private Query<GlobalEditsCounter> updateQueryCounter;
    private UpdateOperations<GlobalEditsCounter> opsCounter;
    private boolean renderNextButton = true;
    private int countUpdated;
    private PushContext pushContext;

    public int getCountUpdated() {
        return countUpdated;
    }

    public void setCountUpdated(int countUpdated) {
        this.countUpdated = countUpdated;
    }

    public TableMatchesBean() {
        try {
            System.out.println("new TableMatchesBean!");
            listCloseMatchesDisplayed.clear();
            setClosematchesOriginal.clear();
            optionChosen = 4;


            //RETRIEVE MATCHES FROM THIS UUID
//            setClosematchesOriginal.addAll(ControllerBean.ds.find(CloseMatchBean.class).field("uuid").equal(ControllerBean.uuid.toString()).asList());
            setClosematchesOriginal = ControllerBean.getSetCloseMatches();

            System.out.println("number of ambiguous cases: " + setClosematchesOriginal.size());

            ControllerBean.pushCounter();

            Iterator<CloseMatchBean> setClosematchesOriginalIterator = setClosematchesOriginal.descendingIterator();
            if (setClosematchesOriginalIterator.hasNext()) {
                listCloseMatchesDisplayed.add(setClosematchesOriginalIterator.next());
                setMergedAuthor(listCloseMatchesDisplayed.get(0).getAuthor3());
                setClosematchesOriginalIterator.remove();

            }

        } catch (NullPointerException e) {
            System.err.println("There was no ambiguous names in the author names");
            System.err.println("NullPointerException: " + e.getMessage());

        }

    }

    public String next_() {


        System.out.println("curr button selected is: " + optionChosen);
        System.out.println("curr value of the suggestion field is: " + mergedAuthor);
        save(listCloseMatchesDisplayed.get(0));
        listCloseMatchesDisplayed.clear();

        Iterator<CloseMatchBean> setClosematchesOriginalIterator = setClosematchesOriginal.descendingIterator();
        if (setClosematchesOriginalIterator.hasNext()) {

            listCloseMatchesDisplayed.add(setClosematchesOriginalIterator.next());
            setMergedAuthor(listCloseMatchesDisplayed.get(0).getAuthor3());
            setClosematchesOriginalIterator.remove();
            System.out.println("still this number of closeMatch to treat in the set:" + setClosematchesOriginal.size());

            return null;

        } else {
            System.out.println("no more close Match in the set, moving to the page for final check");
            return "finalcheck?faces-redirect=true";

        }


    }

    public void save(CloseMatchBean closeMatch) {

//        System.out.println("Option chosen is: " + optionChosen);

        switch (optionChosen) {

            case 1: // keep both
//                System.out.println("we keep both");
                ControllerBean.setMapLabels.add(new MapLabels(closeMatch.getAuthor1(), closeMatch.getAuthor1()));
                ControllerBean.setMapLabels.add(new MapLabels(closeMatch.getAuthor2(), closeMatch.getAuthor2()));

                //persisting these edits permanently
                updateQuery = ControllerBean.ds.createQuery(PersistingEdit.class).field("reference").equal(ControllerBean.getSearch().getFullnameWithComma());
                updateQuery.field("originalForm").equal(closeMatch.getAuthor1());
                updateQuery.field("editedForm").equal(closeMatch.getAuthor1());
                ops = ControllerBean.ds.createUpdateOperations(PersistingEdit.class).inc("counter", 1);
                ControllerBean.ds.update(updateQuery, ops, true);


                updateQuery = ControllerBean.ds.createQuery(PersistingEdit.class).field("reference").equal(ControllerBean.getSearch().getFullnameWithComma());
                updateQuery.field("originalForm").equal(closeMatch.getAuthor2());
                updateQuery.field("editedForm").equal(closeMatch.getAuthor2());
                ops = ControllerBean.ds.createUpdateOperations(PersistingEdit.class).inc("counter", 1);
                ControllerBean.ds.update(updateQuery, ops, true);

                updateQueryCounter = ControllerBean.ds.createQuery(GlobalEditsCounter.class);
                opsCounter = ControllerBean.ds.createUpdateOperations(GlobalEditsCounter.class).inc("globalCounter", 2);
                ControllerBean.ds.update(updateQueryCounter, opsCounter, true);
                ControllerBean.pushCounter();



                System.out.println("global counter for edit \"we keep "
                        + closeMatch.getAuthor1()
                        + "as a distinct name: "
                        + ControllerBean.ds.find(PersistingEdit.class).field("reference").equal(ControllerBean.getSearch().getFullnameWithComma())
                        .field("originalForm").equal(closeMatch.getAuthor1())
                        .field("editedForm").equal(closeMatch.getAuthor1()).get().getCounter());

                break;

            case 2: // delete both
//                System.out.println("we delete both");

                //persisting these edits permanently
                updateQuery = ControllerBean.ds.createQuery(PersistingEdit.class).field("reference").equal(ControllerBean.getSearch().getFullnameWithComma());
                updateQuery.field("originalForm").equal(closeMatch.getAuthor1());
                updateQuery.field("editedForm").equal("deleted");
                ops = ControllerBean.ds.createUpdateOperations(PersistingEdit.class).inc("counter", 1);
                ControllerBean.ds.update(updateQuery, ops, true);

                updateQuery = ControllerBean.ds.createQuery(PersistingEdit.class).field("reference").equal(ControllerBean.getSearch().getFullnameWithComma());
                updateQuery.field("originalForm").equal(closeMatch.getAuthor2());
                updateQuery.field("editedForm").equal("deleted");
                ops = ControllerBean.ds.createUpdateOperations(PersistingEdit.class).inc("counter", 1);
                ControllerBean.ds.update(updateQuery, ops, true);

                updateQueryCounter = ControllerBean.ds.createQuery(GlobalEditsCounter.class);
                opsCounter = ControllerBean.ds.createUpdateOperations(GlobalEditsCounter.class).inc("globalCounter", 2);
                ControllerBean.ds.update(updateQueryCounter, opsCounter, true);
                ControllerBean.pushCounter();


                break;

            case 3: //merge them

                if (mergedAuthor.equals(ControllerBean.getSearch().getFullnameWithComma())) {
                    break;
                }
                mergedAuthor = mergedAuthor.replace("  ", " ");
                System.out.println("label 1: \"" + closeMatch.getAuthor1() + "\"");
                System.out.println("label 2: \"" + closeMatch.getAuthor2() + "\"");
                System.out.println("merged into: \"" + mergedAuthor + "\"");

                ControllerBean.setMapLabels.add(new MapLabels(closeMatch.getAuthor1(), mergedAuthor));
                ControllerBean.setMapLabels.add(new MapLabels(closeMatch.getAuthor2(), mergedAuthor));

                //persisting these edits permanently
                //we persist only the edits if the merged solution is different from the original.

                if (closeMatch.getAuthor2().equals(mergedAuthor)) {
                    updateQuery = ControllerBean.ds.createQuery(PersistingEdit.class).field("reference").equal(ControllerBean.getSearch().getFullnameWithComma());
                    updateQuery.field("originalForm").equal(closeMatch.getAuthor1());
                    updateQuery.field("editedForm").equal(mergedAuthor);
                    ops = ControllerBean.ds.createUpdateOperations(PersistingEdit.class).inc("counter", 1);
                    ControllerBean.ds.update(updateQuery, ops, true);

                    updateQueryCounter = ControllerBean.ds.createQuery(GlobalEditsCounter.class);
                    opsCounter = ControllerBean.ds.createUpdateOperations(GlobalEditsCounter.class).inc("globalCounter", 1);
                    ControllerBean.ds.update(updateQueryCounter, opsCounter, true);
                    ControllerBean.pushCounter();

                    System.out.println("global counter of edits after a merge: " + ControllerBean.ds.find(GlobalEditsCounter.class).get().getGlobalCounter());


                    break;
                }
                if (closeMatch.getAuthor1().equals(mergedAuthor)) {
                    updateQuery = ControllerBean.ds.createQuery(PersistingEdit.class).field("reference").equal(ControllerBean.getSearch().getFullnameWithComma());
                    updateQuery.field("originalForm").equal(closeMatch.getAuthor2());
                    updateQuery.field("editedForm").equal(mergedAuthor);
                    ops = ControllerBean.ds.createUpdateOperations(PersistingEdit.class).inc("counter", 1);
                    try {
                        ControllerBean.ds.update(updateQuery, ops, true);
                    } catch (com.mongodb.MongoException e) {
                        System.out.println("exception with MongoDB when saving the mergedAuthor");
                    }

                    updateQueryCounter = ControllerBean.ds.createQuery(GlobalEditsCounter.class);
                    opsCounter = ControllerBean.ds.createUpdateOperations(GlobalEditsCounter.class).inc("globalCounter", 1);
                    ControllerBean.ds.update(updateQueryCounter, opsCounter, true);
                    ControllerBean.pushCounter();

                    System.out.println("global counter of edits after a merge: " + ControllerBean.ds.find(GlobalEditsCounter.class).get().getGlobalCounter());

                    break;
                }
                updateQuery = ControllerBean.ds.createQuery(PersistingEdit.class).field("reference").equal(ControllerBean.getSearch().getFullnameWithComma());
                updateQuery.field("originalForm").equal(closeMatch.getAuthor1());
                updateQuery.field("editedForm").equal(mergedAuthor);
                ops = ControllerBean.ds.createUpdateOperations(PersistingEdit.class).inc("counter", 1);
                ControllerBean.ds.update(updateQuery, ops, true);


                updateQuery = ControllerBean.ds.createQuery(PersistingEdit.class).field("reference").equal(ControllerBean.getSearch().getFullnameWithComma());
                updateQuery.field("originalForm").equal(closeMatch.getAuthor2());
                updateQuery.field("editedForm").equal(mergedAuthor);
                ops = ControllerBean.ds.createUpdateOperations(PersistingEdit.class).inc("counter", 1);
                ControllerBean.ds.update(updateQuery, ops, true);

                updateQueryCounter = ControllerBean.ds.createQuery(GlobalEditsCounter.class);
                opsCounter = ControllerBean.ds.createUpdateOperations(GlobalEditsCounter.class).inc("globalCounter", 2);
                ControllerBean.ds.update(updateQueryCounter, opsCounter, true);
                ControllerBean.pushCounter();

//                System.out.println("global counter of edits after a merge: " + ControllerBean.ds.find(GlobalEditsCounter.class).get().getGlobalCounter());



                break;

            case 4: //merge them the two, but don't persist them.

                ControllerBean.setMapLabels.add(new MapLabels(closeMatch.getAuthor1(), mergedAuthor));
                System.out.println("mapLabel added: " + closeMatch.getAuthor1() + ", " + mergedAuthor);
                ControllerBean.setMapLabels.add(new MapLabels(closeMatch.getAuthor2(), mergedAuthor));
                System.out.println("mapLabel added: " + closeMatch.getAuthor2() + ", " + mergedAuthor);

                break;


        }


    }

    public CloseMatchBean getCloseMatchBean_() {

        return listCloseMatchesDisplayed.get(0);
    }

    public String getMergedAuthor() {
        return mergedAuthor;
    }

    public void setMergedAuthor(String inputMergedAuthor) {
        this.mergedAuthor = inputMergedAuthor.trim();
//        System.out.println("mergedAuthor set to: " + this.mergedAuthor);
    }

    public Integer getOptionChosen() {
        return this.optionChosen;
    }

    public void setOptionChosen(Integer optionChosen) {
        this.optionChosen = optionChosen;
//        System.out.println("option chosen: " + this.optionChosen);
        if (this.optionChosen.equals(3)) {
            renderInputMerge = true;
//            System.out.println("toggled on");
        } else {
            renderInputMerge = false;
//            System.out.println("toggled off");
        }

        renderNextButton = true;
    }

    public boolean isRenderInputMerge() {
        return renderInputMerge;
    }

    public void setRenderInputMerge(boolean renderInputMerge) {
        this.renderInputMerge = renderInputMerge;
    }

    public boolean isRenderNextButton() {
        return renderNextButton;
    }

    public void setRenderNextButton(boolean renderNextButton) {
        this.renderNextButton = renderNextButton;
    }
//    public synchronized boolean pushCounter() {
//        countUpdated = ControllerBean.ds.find(GlobalEditsCounter.class).get().getGlobalCounter();
////        countUpdated++;
//        System.out.println("counter in pushCounter method is:" + countUpdated);
//
//        pushContext.push("/counter", String.valueOf(countUpdated));
//
//        return true;
//    }
}
