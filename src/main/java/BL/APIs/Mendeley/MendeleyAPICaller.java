package BL.APIs.Mendeley;

import Controller.AdminPanel;
import Utils.APIkeys;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.text.WordUtils;

public class MendeleyAPICaller {

    private static String quidamFirstName;
    private static String quidamLastName;
    private static String APIkey;
    public static boolean debug = AdminPanel.mendeleyDebugStateTrueOrFalse();
    private static String currLine;
    private static BufferedReader APIresult;
    private static ContainerMendeleyDocuments container;

    public static ContainerMendeleyDocuments run(String firstname, String lastname) throws Exception {

        quidamFirstName = firstname.replaceAll(" ", "%20");
        quidamLastName = lastname.replaceAll(" ", "%20");
        APIkey = APIkeys.getMendeleyAPIkey();

        if (!debug) {
            String APIcall = "http://api.mendeley.com/oapi/documents/authored/" + "%22" + quidamFirstName + "%20" + quidamLastName + "%22" + "/" + "?consumer_key=" + APIkey + "&items=500";
            System.out.println("Mendeley API call is:");
            System.out.println(APIcall);

            URL mendeley = new URL(APIcall);
            URLConnection mc = mendeley.openConnection();


            try {
                APIresult = new BufferedReader(
                        new InputStreamReader(
                        mc.getInputStream()));
            } catch (java.net.ConnectException e) {
                System.out.println("connection to API failed");
            } catch (IOException e) {

                System.err.println("Caught IOException: " + e.getMessage());
                System.out.println("Mendeley API was unavailable, 0 docs returned");

                return container;
            }

            currLine = APIresult.readLine();
            currLine = convertUnicode(currLine);
//            currLine = RemoveNonASCII.remove(currLine);
            currLine = WordUtils.capitalize(currLine);
//            System.out.println("Mendeley API response: " + currLine);

            //            System.out.println("Mendely API response: ");
//            System.out.println(currLine);

            container = new Gson().fromJson(currLine, ContainerMendeleyDocuments.class);

            APIresult.close();

        }
        if (debug) {
            String debugLine = getFileContents("D:\\Docs Pro Clement\\E-projects\\Olympics\\mendeleyAndrea.xml");
//            debugLine = RemoveNonASCII.remove(debugLine);
            container = new Gson().fromJson(debugLine, ContainerMendeleyDocuments.class);
//            System.out.println("size of container is: "+container.getDocuments().size());
//            currLine = WordUtils.capitalize(currLine);

        }


        if (container == null) {
            System.out.println("no docs returned by the Mendeley API call!");
        }
        return container;

    }

    private static String convertUnicode(String line) {
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
    
        private static String getFileContents(String filename)
            throws IOException, FileNotFoundException {
        File file = new File(filename);
        StringBuilder contents = new StringBuilder();

        BufferedReader input = new BufferedReader(new FileReader(file));

        try {
            String line;

            while ((line = input.readLine()) != null) {
                contents.append(line);
                contents.append(System.getProperty("line.separator"));
            }
        } finally {
            input.close();
        }

        return contents.toString();
    }

}
