/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Controller.ControllerBean;
import Utils.Timer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author C. Levallois
 */
@ManagedBean
@ViewScoped
public class ProgressBarMessenger implements Serializable {

    private static StringBuilder sb = new StringBuilder();
    private List<Callable<Integer>> calls = ControllerBean.calls;
    private List<Future<Integer>> futures;
    private Callable<Integer> currCall;
    private boolean callsComplete = false;
    private boolean processingComplete = false;
    private int countCalls;
    private ExecutorService pool;
    private List<Future<Integer>> listResults;
    private static String worldCatProgress = "not set yet";
    private String wcp = "not set yet";

    public ProgressBarMessenger() {

        sb = new StringBuilder();
        countCalls = 0;
        sb.append("<p>This is a progress bar - will continue to improve...</p>");
        sb.append("<p>Looking up information on ").append(ControllerBean.getSearch().getFullname()).append(" on very large databases... </p>");
        sb.append("<p>All API calls made at once</p>");
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
    
    public static String getWorldCatProgress(){
        return String.valueOf(worldCatProgress);
    }

    public static void setWorldCatProgress(String count){
        worldCatProgress = count;
    }

    public String processCalls() throws InterruptedException {
        System.out.println("APIs calls started");
        pool = Executors.newFixedThreadPool(10);
        futures = new ArrayList();
        Iterator<Callable<Integer>> callsIterator = calls.iterator();
        while (callsIterator.hasNext()) {
            countCalls++;
            currCall = callsIterator.next();

            Future<Integer> future = pool.submit(currCall);
            futures.add(future);
            System.out.println("new Call submitted: " + countCalls);
            callsIterator.remove();
        }
        callsComplete = true;
        //            Timer.waitSeconds(5);
        System.out.println("API calls have been placed");

        return null;



    }

    public String checkUpdates() throws InterruptedException {

        if (futures == null) {
            return null;
        }

        Iterator<Future<Integer>> futuresIterator = futures.iterator();
        while (futuresIterator.hasNext()) {
            Future<Integer> future = futuresIterator.next();
            if (!future.isDone()) {
                System.out.println("some API calls have not returned yet");
                return null;
            }
        }
        callsComplete = true;
        updateMsg("The search across the web is over. We found xx documents.<br> We will now proceed to the aggregation of the results.");
        System.out.println("all API calls returned");

        String nextPage = ControllerBean.treatmentAPIresults();

        if (!processingComplete) {
            processingComplete = true;
            Timer.waitSeconds(6);
            return null;
        }
        System.out.println(
                "nextpage:" + nextPage);
        return nextPage;
    }

    private String getAPILaius(int nb) {
        String laius = null;
        switch (nb) {
            case 1:
                laius = "<p>Looking at Arxiv now...</p>";
                break;
            case 2:
                laius = "<p>search of Arxiv is over.</p><p>  Looking at Mendeley now...</p>";
                break;
        }
        return laius;

    }

    public String getWcp() {
        return worldCatProgress;
    }

    public void setWcp(String wcp) {
        this.wcp = worldCatProgress;
    }
    
    
}
