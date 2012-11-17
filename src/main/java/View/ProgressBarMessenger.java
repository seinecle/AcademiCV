/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Controller.ControllerBean;
import Utils.Timer;
import Model.Document;
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
    private List<Future<Set<Document>>> futures;
    private Callable<Set<Document>> currCall;
    private List<Callable<Set<Document>>> calls;
    private boolean callsComplete = false;
    private boolean processingComplete = false;
    private int countCalls;
    private ExecutorService pool;
    private List<Future<Set<Document>>> listResults;
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
        Iterator<Callable<Set<Document>>> callsIterator = calls.iterator();
        while (callsIterator.hasNext()) {
            countCalls++;
            currCall = callsIterator.next();

            Future<Set<Document>> future = pool.submit(currCall);
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

        Iterator<Future<Set<Document>>> futuresIterator = futures.iterator();
        while (futuresIterator.hasNext()) {
            Future<Set<Document>> future = futuresIterator.next();
            if (!future.isDone()) {
                System.out.println("some API calls have not returned yet");
                return null;
            }
        }
        futuresIterator = futures.iterator();
        while (futuresIterator.hasNext()) {
            try {
                Future<Set<Document>> future = futuresIterator.next();
                controllerBean.addToSetDocs(future.get());
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
            System.out.println("treatment API data is over!");
            processingComplete = true;
        } else {
            if (!buttonsDisplayed) {
                updateMsg("<br>Cleaning is now complete.<br>");

                if (nextPage.equals("pairscheck?faces-redirect=true")) {
                    toggleButtonCorrections = true;
                    updateMsg("Some co-authors of " + controllerBean.getSearch().getFullname() + " appear to be mispelled. We recommend that you help us make the necessary corrections.<br>");

                }
                if (nextPage.equals("report?faces-redirect=true")) {
                    toggleButtonReport = true;
                }

                if (nextPage.equals("finalcheck?faces-redirect=true")) {
                    toggleButtonFinalCheck = true;
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
