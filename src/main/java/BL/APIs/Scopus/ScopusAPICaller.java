/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.APIs.Scopus;

import BL.APIs.Mendeley.ContainerMendeleyDocuments;
import Controller.AdminPanel;
import Utils.APIkeys;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xml.sax.InputSource;

/**
 *
 * @author C. Levallois
 */
public class ScopusAPICaller {

    private static String quidamFirstName;
    private static String quidamLastName;
    public static boolean debug = AdminPanel.scopusDebugStateTrueOrFalse();
    private static String currLine;
    private static BufferedReader APIresult;
    private static ContainerMendeleyDocuments container;
    private static InputSource is;

    public static BufferedReader run(String firstname, String lastname) throws Exception {

        quidamFirstName = firstname.replaceAll(" ", "%20");
        quidamLastName = lastname.replaceAll(" ", "%20");

        String APIcall = "http://api.elsevier.com/content/search/index:SCIDIR?query=blood&count=25&start=0&view=STANDARD";
        System.out.println("API call is:");
        System.out.println(APIcall);

        URL scopus = new URL(APIcall);
        URLConnection mc = scopus.openConnection();

        mc.setRequestProperty("X-ELS-APIKey", APIkeys.getScopusAPIkey());
        mc.setRequestProperty("X-ELS-ResourceVersion","XOCS");
        mc.setRequestProperty("Accept","application/atom+xml");


        
        try {
            APIresult = new BufferedReader(
                    new InputStreamReader(
                    mc.getInputStream()));
        } catch (java.net.ConnectException e) {
            System.out.println("connection to SCOPUS failed");
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }

        currLine = APIresult.readLine();
        System.out.println("SCOPUS results is: " + currLine);
//            currLine = convertUnicode(currLine);
        is = new InputSource(APIresult);


        return APIresult;


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
