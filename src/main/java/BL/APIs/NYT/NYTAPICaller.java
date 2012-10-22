/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.APIs.NYT;

import Controller.AdminPanel;
import Utils.APIkeys;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author C. Levallois
 */
public class NYTAPICaller {

    private static String quidamFirstName;
    private static String quidamLastName;
    private static String APIkey;
    public static boolean debug = AdminPanel.nytDebugStateTrueOrFalse();
    private static String currLine;
    private static BufferedReader APIresult;
    private static ContainerNYTDocuments container;

    public static ContainerNYTDocuments callAPI(String firstname, String lastname) throws Exception {

        quidamFirstName = firstname.replaceAll(" ", "%20");
        quidamLastName = lastname.replaceAll(" ", "%20");
        APIkey = APIkeys.getNYTAPIkey();
        container = new ContainerNYTDocuments();

        if (!debug) {
            String APIcall = "http://api.nytimes.com/svc/search/v1/article?query=" + "%22" + quidamFirstName + "%20" + quidamLastName + "%22" + "&api-key=" + APIkey;
            System.out.println("API call is:");
            System.out.println(APIcall);

            URL nyt = new URL(APIcall);
            URLConnection mc = nyt.openConnection();

            try {
                APIresult = new BufferedReader(
                        new InputStreamReader(
                        mc.getInputStream()));
            } catch (java.net.ConnectException e) {
                System.out.println("connection to API failed");
            } catch (IOException e) {

                System.err.println("Caught IOException: " + e.getMessage());
                System.out.println("NYT API was unavailable, 0 docs returned");
                return container;
            }

            currLine = APIresult.readLine();
            currLine = convertUnicode(currLine);
//            currLine = RemoveNonASCII.remove(currLine);
//            currLine = WordUtils.capitalize(currLine);
            System.out.println(currLine);

            //            System.out.println("Mendely API response: ");
//            System.out.println(currLine);

            container = new Gson().fromJson(currLine, ContainerNYTDocuments.class);

            APIresult.close();
        }

        return container;

    }

    static String convertUnicode(String line) {
        final Matcher m = Pattern.compile("\\\\u(.{4})").matcher(line);
        final StringBuffer b = new StringBuffer();
        while (m.find()) {
//                System.out.println("unicode found");
//                System.out.println("replacement for unicode: "+String.valueOf(((char) Integer.parseInt(m.group(1), 16))));
            m.appendReplacement(b, String.valueOf(((char) Integer.parseInt(m.group(1), 16))));
        }
        m.appendTail(b);
        return b.toString();

    }
}