/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Controller.ControllerBean;
import Model.Author;
import Model.Document;
import Utils.PairSimple;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author C. Levallois
 */
@ManagedBean
@ViewScoped
public class ProgressBarMessenger implements Serializable {

    private static StringBuilder sb = new StringBuilder();
    private List<Future<PairSimple<Set<Document>, Author>>> futures;
    private Callable<PairSimple<Set<Document>, Author>> currCall;
    private List<Callable<PairSimple<Set<Document>, Author>>> calls;
    private boolean callsComplete = false;
    private boolean processingComplete = false;
    private int countCalls;
    private ExecutorService pool;
    private List<Future<PairSimple<Set<Document>, Author>>> listResults;
    private static StringBuilder msg;
    private String progressMessage = "not set yet";
    @ManagedProperty("#{controllerBean}")
    private ControllerBean controllerBean;
    private boolean firstProcessingUpdate = true;
    private boolean toggleButtonCorrections = false;
    private String nextPage = null;
    private boolean toggleButtonReport = false;
    private boolean toggleButtonFinalCheck = false;
    private boolean buttonsDisplayed = false;
    private Author search;
    private int nbCase;

    public void setcontrollerBean(ControllerBean controllerBean) {
        this.controllerBean = controllerBean;
    }

    public ProgressBarMessenger() {
    }

    @PostConstruct
    private void init() {
        sb = new StringBuilder();
        msg = new StringBuilder();
        countCalls = 0;
        search = controllerBean.getSearch();
        sb.append("<p>Looking up information on ").append(controllerBean.getSearch().getFullname()).append(" on very large databases... </p>");
//        sb.append("<p>Currently searching WorldCat: a catalogue of publications in thousands of libraries in the world </p>");
//        sb.append("<p>(please be patient while it loads...)</p>");
        System.out.println("new ProgressBarMessenger initialized!");

    }

    public String returnMsg() {
        return sb.toString();
    }

    public static void updateMsg(String msg) {
        sb.append(msg);
        sb.append(" ");
    }

    public static String getProgress() {
        return msg.toString();
    }

    public static void setProgress(String newMsg) {
        msg.append(newMsg);
    }

    public String processCalls() throws InterruptedException {
        calls = controllerBean.getCalls();
        System.out.println("APIs calls started");
        pool = Executors.newFixedThreadPool(10);
        futures = new ArrayList();
        Iterator<Callable<PairSimple<Set<Document>, Author>>> callsIterator = calls.iterator();
        while (callsIterator.hasNext()) {
            countCalls++;
            currCall = callsIterator.next();

            Future<PairSimple<Set<Document>, Author>> future = pool.submit(currCall);
            futures.add(future);
            System.out.println("new Call submitted: " + countCalls);
            callsIterator.remove();
        }
        //            Timer.waitSeconds(5);
        System.out.println("API calls have been placed");

        return null;
    }

    public String checkUpdates() throws InterruptedException {

        if (futures == null) {
            return null;
        }

        Iterator<Future<PairSimple<Set<Document>, Author>>> futuresIterator = futures.iterator();
        while (futuresIterator.hasNext()) {
            Future<PairSimple<Set<Document>, Author>> future = futuresIterator.next();
            if (!future.isDone()) {
                System.out.println("some API calls have not returned yet");
                return null;
            }
        }
        futuresIterator = futures.iterator();
        while (futuresIterator.hasNext()) {
            try {
                Future<PairSimple<Set<Document>, Author>> future = futuresIterator.next();
                PairSimple<Set<Document>, Author> ps = future.get();
                controllerBean.addToSetDocs(ps.getLeft());
                if (ps.getRight().getBirthYear() != 0 & ps.getRight().getBirthYear() != null) {
                    System.out.println("year of birth added: " + ps.getRight().getBirthYear());
                    search.setBirthYear(ps.getRight().getBirthYear());
                    controllerBean.setSearch(search);
                }
                futuresIterator.remove();
            } catch (ExecutionException ex) {
                Logger.getLogger(ProgressBarMessenger.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("all API calls returned");

        if (firstProcessingUpdate) {
            updateMsg("The search across the web is over. We found " + controllerBean.getSetDocs().size() + " documents.<br>"
                    + "<br>"
                    + "Performing important cleaning operations. If many publications were found, this can take a few seconds, be patient");
            firstProcessingUpdate = false;
            return null;
        } else {
            if (!processingComplete) {
                updateMsg(".");
            }
        }


        if (!processingComplete) {
            nextPage = controllerBean.treatmentAPIresults();
            if (nextPage.equals("pairscheck?faces-redirect=true")){
                nbCase = 1;
                }
            else if(nextPage.equals("report?faces-redirect=true")){
                nbCase = 2;
            }
            else if (nextPage.equals("finalcheck?faces-redirect=true")){
                nbCase = 3;
            }
            else {
                nbCase = -1;
            }
            
            System.out.println("treatment API data is over!");
            processingComplete = true;
        } else {
            if (!buttonsDisplayed) {
                updateMsg("<br>Cleaning is now complete.<br>");
                switch (nbCase) {
                    case 1:
                        toggleButtonCorrections = true;
                        updateMsg("Some co-authors of " + controllerBean.getSearch().getFullname() + " appear to be mispelled. We recommend that you help us make the necessary corrections.<br>");
                        break;
                    case 2:
                        toggleButtonReport = true;
                        break;
                    case 3:
                        toggleButtonFinalCheck = true;
                        break;
                    default:
                        return nextPage;
                }
                
                
                buttonsDisplayed = true;
            }
        }
        return null;
    }

    public String getProgressMessage() {
//        System.out.println("getting progress msg");
        return msg.toString();
    }

    public void setProgressMessage(String newMsg) {
//        System.out.println("setting progress msg");
        this.progressMessage = msg.toString();
    }

    public boolean isToggleButtonCorrections() {
        return toggleButtonCorrections;
    }

    public boolean isToggleButtonReport() {
        return toggleButtonReport;
    }

    public boolean isToggleButtonFinalCheck() {
        return toggleButtonFinalCheck;
    }

    public String getNextPage() {
        return nextPage;
    }
}
