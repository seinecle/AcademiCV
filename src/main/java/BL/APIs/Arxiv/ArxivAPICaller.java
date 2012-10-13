/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.APIs.Arxiv;

import Controller.AdminPanel;
import BL.APIs.Mendeley.ContainerMendeleyDocuments;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xml.sax.InputSource;

/**
 *
 * @author C. Levallois
 */
public class ArxivAPICaller {

    private static String quidamFirstName;
    private static String quidamLastName;
    public static boolean debug = AdminPanel.arxivDebugStateTrueOrFalse();
    private static String currLine;
    private static BufferedReader APIresult;
    private static ContainerMendeleyDocuments container;
    private static InputSource is;

    public static BufferedReader run(String firstname, String lastname) throws Exception {

        quidamFirstName = firstname.replaceAll(" ", "%20");
        quidamLastName = lastname.replaceAll(" ", "%20");

        String APIcall = "http://export.arxiv.org/api/query?search_query=au:" + quidamFirstName + "+" + quidamLastName + "&max_results=200";
        System.out.println("API call is:");
        System.out.println(APIcall);

        URL arxiv = new URL(APIcall);
        URLConnection mc = arxiv.openConnection();

        try {
            APIresult = new BufferedReader(
                    new InputStreamReader(
                    mc.getInputStream()));
        } catch (java.net.ConnectException e) {
            System.out.println("connection to ARXIV failed");
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
            System.exit(-1);
        }

        currLine = APIresult.readLine();
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
