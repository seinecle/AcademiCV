/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.APIs.WorldCatIdentities;

import Controller.AdminPanel;
import java.io.BufferedReader;
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
public class WorldCatAPICallerWithIdentityCode {

    public static boolean debug;
    private static BufferedReader APIresult;
    private static InputSource is;

    public static InputSource run(String currIdentity) throws Exception {


        debug = AdminPanel.worldcatDebugStateTrueOrFalse();
        StringBuilder content;
        String line;
        String lineSeparator = System.getProperty("line.separator");
        String result;



        if (!debug) {

            
            String APIcall = "http://worldcat.org"+ currIdentity + "/identity.xml";
            System.out.println("WorldCat Identity API call is:");
            System.out.println(APIcall);

            URL worldcat = new URL(APIcall);
            URLConnection mc = worldcat.openConnection();

            try {
                APIresult = new BufferedReader(
                        new InputStreamReader(
                        mc.getInputStream()));
            } catch (java.net.ConnectException e) {
                System.out.println("connection to WORLDCAT failed");
            } catch (IOException e) {
                System.err.println("Caught IOException: " + e.getMessage());
            }
//            while ((line = APIresult.readLine()) != null) {
//                System.out.println(line);
//            }

            is = new InputSource(APIresult);

        }
        if (debug) {
            System.out.println("WorldCat API call in debugging mode");
            content = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader("D:\\Docs Pro Clement\\E-projects\\Olympics\\worldcatClement.txt"));
            while ((line = br.readLine()) != null) {
                content.append(line);
                content.append(lineSeparator);
            }
            result = content.toString();
            is = new InputSource(new StringReader(result));
        }

//        System.out.println("WORLDCAT results are: " + result);
//            currLine = convertUnicode(currLine);


        return is;


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
