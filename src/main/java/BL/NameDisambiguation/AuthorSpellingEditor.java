/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.NameDisambiguation;

import Controller.AdminPanel;
import Controller.ControllerBean;
import Model.Author;
import Model.PersistingEdit;
import Utils.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author C. Levallois
 */
public class AuthorSpellingEditor {

    TreeSet<Author> setCloseMatches = new TreeSet();
    TreeSet<Author> setAuthorsWithEdits = new TreeSet();
    ArrayList authorsInOneDoc;
    Author currAuth;
    String mainFirstName;
    String mainLastName;
    boolean debug;
    private TreeMap<String, String> mapLabels = new TreeMap();
    private boolean atLeastOneMatchFound = false;
    private String[] arrayTermsInFullnameInAuthor1;
    private String[] arrayTermsInFullnameInAuthor2;
    private HashSet<String> setTermsInFullnameAuthor1;
    private HashSet<String> setTermsInFullnameAuthor2;
    private String[] arrayTermsInSurnameInAuthor1;
    private String[] arrayTermsInSurnameInAuthor2;
    private HashSet<String> setTermsInSurnameAuthor1;
    private HashSet<String> setTermsInSurnameAuthor2;
    private HashSet<String> intersectFullNamesAuthor1And2;
    private HashMap<String, Pair<String, Integer>> mapEdits;
    private boolean wisdomCrowd;
    private Set<Author> setAuthorsOriginal;
    private Author search;

    public AuthorSpellingEditor(Set<Author> setAuthorsOriginal, boolean wisdomCrowd, Author search) {

        this.search = search;
        this.wisdomCrowd = wisdomCrowd;
        this.setAuthorsOriginal = setAuthorsOriginal;

    }

    public Set<Author> check() {

        debug = AdminPanel.wisdomCrowdsDebugStateTrueOrFalse();

        //***********
        //if the user has selected the wisdom of the crowds in the UI
        //retrieve the persisted edits corresponding to the author currently being searched
        //***********

        if (wisdomCrowd) {
            List<PersistingEdit> listEdits = ControllerBean.ds.find(PersistingEdit.class).field("reference").equal(search.getFullnameWithComma()).field("counter").greaterThan(1).asList();
            mapEdits = new HashMap();
            int elementCounter;
            String elementEditedForm;
            String elementOriginalForm;
            int elementCounterInMap;

            for (PersistingEdit element : listEdits) {
                elementCounter = element.getCounter();
//            System.out.println("elementCounter: " + elementCounter);
                elementEditedForm = element.getEditedForm();
//            System.out.println("elementEditedForm: " + elementEditedForm);
                elementOriginalForm = element.getOriginalForm();
//            System.out.println("elementOriginalForm: " + elementOriginalForm);

                if (!mapEdits.containsKey(elementOriginalForm)) {
                    mapEdits.put(elementOriginalForm, new Pair(elementEditedForm, elementCounter));
                } else {
                    elementCounterInMap = mapEdits.get(elementOriginalForm).getRight();
                    if (elementCounterInMap < elementCounter) {
                        mapEdits.put(elementOriginalForm, new Pair(elementEditedForm, elementCounter));
                    }
                }
            }
        }



        //***********
        // makes the necessary edits, retrieved in the step before from the database, to the set of Authors
        //***********

        Iterator<Author> setAuthorsOriginalIterator = setAuthorsOriginal.iterator();
        while (setAuthorsOriginalIterator.hasNext()) {
            currAuth = setAuthorsOriginalIterator.next();
            if (wisdomCrowd && !debug) {
                boolean editAlreadyExists = mapEdits.containsKey(currAuth.getFullnameWithComma());
                if (editAlreadyExists) {
                    String editedForm = mapEdits.get(currAuth.getFullnameWithComma()).getLeft();
                    String[] terms = editedForm.split(",");
                    currAuth.setForename(terms[0].trim());
                    currAuth.setSurname(terms[1].trim());
                    currAuth.setFullname(terms[0].trim() + " " + terms[1].trim());
                }
            }
            //we don't keep in the set of authors the badly spelled versions of the author's name
            if (StringUtils.stripAccents(currAuth.getFullname()).toLowerCase().replaceAll("-", " ").trim().equals(StringUtils.stripAccents(search.getFullname().toLowerCase().replaceAll("-", " ").trim()))) {
                continue;
            }

            setAuthorsWithEdits.add(currAuth);
        }
        return setAuthorsWithEdits;
    }
}