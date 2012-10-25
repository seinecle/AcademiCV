package BL.APIs.Mendeley;

import Controller.AdminPanel;
import Utils.APIkeys;
import Utils.RemoveNonASCII;
import com.google.gson.Gson;
import java.io.BufferedReader;
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
            String APIcall = "http://api.mendeley.com/oapi/documents/authored/" + "%22" + quidamFirstName + "%20" + quidamLastName + "%22" + "/" + "?consumer_key=" + APIkey + "&items=1000";
            System.out.println("API call is:");
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
//            System.out.println(currLine);

            //            System.out.println("Mendely API response: ");
//            System.out.println(currLine);

            container = new Gson().fromJson(currLine, ContainerMendeleyDocuments.class);

            APIresult.close();

        }
        if (debug) {
            String debugLine = "{\"documents\":[{\"uuid\":\"4d766090-6d02-11df-a2b2-0026b95e3eb7\",\"title\":\"Looking at a digital research data archive - Visual interfaces to EASY\",\"publication_outlet\":\"Journal of Informetrics\",\"year\":2009,\"mendeley_url\":\"http:\\/\\/www.mendeley.com\\/research\\/visual-conceptualizations-and-models-of-science\\/\",\"doi\":null,\"authors\":[{\"forename\":\"Katy\",\"surname\":\"Boerner\"},{\"forename\":\"Andrea\",\"surname\":\"Schrnhorst\"}]},{\"uuid\":\"fc7ed690-6d01-11df-a2b2-0026b95e3eb7\",\"title\":\"Maps of the academic web in the European Higher Education Area \\u2014 an exploration of visual web indicators\",\"publication_outlet\":\"Scientometrics\",\"year\":2007,\"mendeley_url\":\"http:\\/\\/www.mendeley.com\\/research\\/maps-of-the-academic-web-in-the-european-higher-education-area-an-exploration-of-visual-web-indicators\\/\",\"doi\":\"10.1007\\/s11192-008-0218-9\",\"authors\":[{\"forename\":\"Jose Luis\",\"surname\":\"Ortega\"},{\"forename\":\"Isidro\",\"surname\":\"Aguillo\"},{\"forename\":\"Viv\",\"surname\":\"Cothey\"},{\"forename\":\"Andrea\",\"surname\":\"Scharnhorst\"}]},{\"uuid\":\"a300a920-8e21-11e1-ac31-0024e8453de6\",\"title\":\"Evolution of Wikipedia's Category Structure\",\"publication_outlet\":\"Advances in Complex Systems\",\"year\":2012,\"mendeley_url\":\"http:\\/\\/www.mendeley.com\\/research\\/evolution-wikipedia-s-category-structure\\/\",\"doi\":\"10.1142\\/S0219525912500683\",\"authors\":[{\"forename\":\"Krzysztof\",\"surname\":\"Suchecki\"},{\"forename\":\"Alkim Almila Akdag\",\"surname\":\"Salah\"},{\"forename\":\"Cheng\",\"surname\":\"Gao\"},{\"forename\":\"Andrea\",\"surname\":\"Scharnhorst\"}]},{\"uuid\":\"6045a870-d3a4-11e0-83ce-0024e8453de6\",\"title\":\"Modeling science: studying the structure and dynamics of science\",\"publication_outlet\":\"Scientometrics\",\"year\":2011,\"mendeley_url\":\"http:\\/\\/www.mendeley.com\\/research\\/modeling-science-studying-structure-dynamics-science\\/\",\"doi\":\"10.1007\\/s11192-011-0429-3\",\"authors\":[{\"forename\":\"Katy\",\"surname\":\"B\\u00f6rner\"},{\"forename\":\"Wolfgang\",\"surname\":\"Gl\\u00e4nzel\"},{\"forename\":\"Andrea\",\"surname\":\"Scharnhorst\"},{\"forename\":\"Peter\",\"surname\":\"Besselaar\"}]},{\"uuid\":\"1fb3e7c0-e1ad-11df-874c-0024e8453de6\",\"title\":\"Tracing scientific influence\",\"publication_outlet\":\"International Journal Dynamics of SocioEconomic Systems\",\"year\":2010,\"mendeley_url\":\"http:\\/\\/www.mendeley.com\\/research\\/tracing-scientific-influence\\/\",\"doi\":null,\"authors\":[{\"forename\":\"Andrea\",\"surname\":\"Scharnhorst\"},{\"forename\":\"Eugene\",\"surname\":\"Garfield\"}]},{\"uuid\":\"2fc9f650-cdd4-11df-922b-0024e8453de6\",\"title\":\"Self-citations, co-authorships and keywords: A new approach to scientists\\u2019 field mobility?\",\"publication_outlet\":\"Scientometrics\",\"year\":2007,\"mendeley_url\":\"http:\\/\\/www.mendeley.com\\/research\\/selfcitations-coauthorships-and-keywords-a-new-approach-to-scientists-field-mobility\\/\",\"doi\":\"10.1007\\/s11192-007-1680-5\",\"authors\":[{\"forename\":\"Iina\",\"surname\":\"Hellsten\"},{\"forename\":\"Renaud\",\"surname\":\"Lambiotte\"},{\"forename\":\"Andrea\",\"surname\":\"Scharnhorst\"},{\"forename\":\"Marcel\",\"surname\":\"Ausloos\"}]},{\"uuid\":\"85c12d80-6d08-11df-a2b2-0026b95e3eb7\",\"title\":\"Modelling Selforganization and Innovation Processes in Networks\",\"publication_outlet\":\"Innovation\",\"year\":2004,\"mendeley_url\":\"http:\\/\\/www.mendeley.com\\/research\\/modelling-selforganization-and-innovation-processes-in-networks\\/\",\"doi\":null,\"authors\":[{\"forename\":\"Ingrid\",\"surname\":\"Hartmann-Sonntag\"},{\"forename\":\"Andrea\",\"surname\":\"Scharnhorst\"},{\"forename\":\"Werner\",\"surname\":\"Ebeling\"}]},{\"uuid\":\"6ba95670-6d08-11df-a2b2-0026b95e3eb7\",\"title\":\"Citation and hyperlink networks\",\"publication_outlet\":\"Current Science\",\"year\":2005,\"mendeley_url\":\"http:\\/\\/www.mendeley.com\\/research\\/citation-and-hyperlink-networks\\/\",\"doi\":null,\"authors\":[{\"forename\":\"Andrea\",\"surname\":\"Scharnhorst\"},{\"forename\":\"Mike\",\"surname\":\"Thelwall\"}]},{\"uuid\":\"f504fb60-6d06-11df-a2b2-0026b95e3eb7\",\"title\":\"Complex Networks and the Web: Insights From Nonlinear Physics\",\"publication_outlet\":\"Journal of Computer-Mediated Communication\",\"year\":2003,\"mendeley_url\":\"http:\\/\\/www.mendeley.com\\/research\\/complex-networks-and-the-web-insights-from-nonlinear-physics\\/\",\"doi\":\"10.1111\\/j.1083-6101.2003.tb00222.x\",\"authors\":[{\"forename\":\"Andrea\",\"surname\":\"Scharnhorst\"}]},{\"uuid\":\"8bb84e20-812c-11df-aedb-0024e8453de8\",\"title\":null,\"publication_outlet\":null,\"year\":null,\"mendeley_url\":\"http:\\/\\/www.mendeley.com\\/research\\/\\/\",\"doi\":null,\"authors\":null},{\"uuid\":\"3c0fa7c0-ccda-11df-922b-0024e8453de6\",\"title\":\"Competition in science and the Matthew core journals\",\"publication_outlet\":\"Scientometrics\",\"year\":2001,\"mendeley_url\":\"http:\\/\\/www.mendeley.com\\/research\\/competition-science-matthew-core-journals\\/\",\"doi\":\"10.1023\\/A:1010508510398\",\"authors\":[{\"forename\":\"Manfred\",\"surname\":\"Bonitz\"},{\"forename\":\"Andrea\",\"surname\":\"Scharnhorst\"}]},{\"uuid\":\"00277950-f3fc-11e0-b69e-0024e8453de6\",\"title\":\"The evolution of knowledge, and its representation in classification systems\",\"publication_outlet\":\"Classification and ontology formal approaches and access to knowledge Proceedings of the International UDC Seminar 1920 September 2011 The Hague The Netherlands\",\"year\":2011,\"mendeley_url\":\"http:\\/\\/www.mendeley.com\\/research\\/evolution-knowledge-representation-classification-systems\\/\",\"doi\":null,\"authors\":[{\"forename\":\"Andrea\",\"surname\":\"Scharnhorst\"},{\"forename\":\"Almila Akdag\",\"surname\":\"Salah\"},{\"forename\":\"Krzysztof\",\"surname\":\"Suchecki\"},{\"forename\":\"Cheng\",\"surname\":\"Gao\"},{\"forename\":\"Richard P\",\"surname\":\"Smiraglia\"}]},{\"uuid\":\"1d45a260-906e-11e0-a443-0024e8453de6\",\"title\":\"Need to categorize: A comparative look at the categories of the Universal Decimal Classification system (UDC) and Wikipedia\",\"publication_outlet\":\"Leonardo\",\"year\":2011,\"mendeley_url\":\"http:\\/\\/www.mendeley.com\\/research\\/need-categorize-comparative-look-categories-universal-deceimal-classification-system-wikipedia\\/\",\"doi\":null,\"authors\":[{\"forename\":\"Almila Akdag\",\"surname\":\"Salah\"},{\"forename\":\"Cheng\",\"surname\":\"Gao\"},{\"forename\":\"Krzysztof\",\"surname\":\"Suchecki\"},{\"forename\":\"Andrea\",\"surname\":\"Scharnhorst\"}]},{\"uuid\":\"9d07ddd0-f606-11e0-b69e-0024e8453de6\",\"title\":\"Visualizing Universes of Knowledge : Designs and Visual Analysis of the UDC\",\"publication_outlet\":\"Proceedings of the International UDC Seminar Classification and Ontology Formal Approaches and Access to Knowledge The Hague 1920 September 2011\",\"year\":2011,\"mendeley_url\":\"http:\\/\\/www.mendeley.com\\/research\\/visualizing-universes-knowledge-design-visual-analysis-udc\\/\",\"doi\":null,\"authors\":[{\"forename\":\"Charles\",\"surname\":\"Van Den Heuvel\"},{\"forename\":\"Almila\",\"surname\":\"Akdag Salah\"},{\"forename\":\"Krzysztof\",\"surname\":\"Suchecki\"},{\"forename\":\"Cheng\",\"surname\":\"Gao\"},{\"forename\":\"Andrea\",\"surname\":\"Scharnhorst\"}]},{\"uuid\":\"6ce23350-6d02-11df-afb8-0026b95d30b2\",\"title\":\"Animating the development of Social Networks over time using a dynamic extension of multidimensional scaling\",\"publication_outlet\":\"Profesional De La Informacion\",\"year\":2008,\"mendeley_url\":\"http:\\/\\/www.mendeley.com\\/research\\/animating-the-development-of-social-networks-over-time-using-a-dynamic-extension-of-multidimensional-scaling\\/\",\"doi\":null,\"authors\":[{\"forename\":\"Loet\",\"surname\":\"Leydesdorff\"},{\"forename\":\"Thomas\",\"surname\":\"Schank\"},{\"forename\":\"Andrea\",\"surname\":\"Scharnhorst\"},{\"forename\":\"Wouter\",\"surname\":\"De Nooy\"}]},{\"uuid\":\"e526ea30-575e-11e0-a61a-0024e8453de6\",\"title\":null,\"publication_outlet\":null,\"year\":null,\"mendeley_url\":\"http:\\/\\/www.mendeley.com\\/research\\/\\/\",\"doi\":null,\"authors\":null},{\"uuid\":\"49509690-6d00-11df-8e55-0026b95e43ca\",\"title\":\"Animaci\\u00f3n de la evoluci\\u00f3n de la revista Social networks en el tiempo utilizando una extensi\\u00f3n din\\u00b4mica del escalado multidimensional\",\"publication_outlet\":\"El Profesional de la Informacion\",\"year\":2008,\"mendeley_url\":\"http:\\/\\/www.mendeley.com\\/research\\/animacin-la-evolucin-la-revista-social-networks-en-el-tiempo-utilizando-una-extensin-dinmica-del-escalado-multidimensional\\/\",\"doi\":\"10.3145\\/epi.2008.nov.04\",\"authors\":[{\"forename\":\"Loet\",\"surname\":\"Leydesdorff\"},{\"forename\":\"Thomas\",\"surname\":\"Schank\"},{\"forename\":\"Andrea\",\"surname\":\"Scharnhorst\"},{\"forename\":\"Wouter\",\"surname\":\"De Nooy\"}]},{\"uuid\":\"9eaf6aa0-f506-11e0-b69e-0024e8453de6\",\"title\":\"Enhancing Scholarly Publishing in the Humanities and Social Sciences: Innovation through Hybrid Forms of Publication\",\"publication_outlet\":\"PKP Scholarly Publishing Conference 2011\",\"year\":2011,\"mendeley_url\":\"http:\\/\\/www.mendeley.com\\/research\\/enhancing-scholarly-publishing-humanities-social-sciences-innovation-through-hybrid-forms-publication\\/\",\"doi\":\"10.2139\\/ssrn.1929687\",\"authors\":[{\"forename\":\"Nicholas W\",\"surname\":\"Jankowski\"},{\"forename\":\"Clifford\",\"surname\":\"Tatum\"},{\"forename\":\"Zuotian\",\"surname\":\"Tatum\"},{\"forename\":\"Andrea\",\"surname\":\"Scharnhorst\"}]},{\"uuid\":\"90481f70-4b78-11e1-86d1-0024e8453de6\",\"title\":\"Enhancing Scholarly Publications: Developing Hybrid Monographs in the Humanities and Social Sciences\",\"publication_outlet\":\"Scholarly and Research Communication\",\"year\":2012,\"mendeley_url\":\"http:\\/\\/www.mendeley.com\\/research\\/enhancing-scholarly-publications-developing-hybrid-monographs-humanities-social-sciences\\/\",\"doi\":\"10.2139\\/ssrn.1982380\",\"authors\":[{\"forename\":\"Nicholas W\",\"surname\":\"Jankowski\"},{\"forename\":\"Andrea\",\"surname\":\"Scharnhorst\"},{\"forename\":\"Clifford\",\"surname\":\"Tatum\"},{\"forename\":\"Zuotian\",\"surname\":\"Tatum\"}]},{\"uuid\":\"98966360-e572-11e0-83ce-0024e8453de6\",\"title\":\"Learning in a Landscape: Simulation-building as Reflexive Intervention\",\"publication_outlet\":\"arXiv11085502\",\"year\":2011,\"mendeley_url\":\"http:\\/\\/www.mendeley.com\\/research\\/learning-landscape-simulationbuilding-reflexive-intervention\\/\",\"doi\":null,\"authors\":[{\"forename\":\"Anne\",\"surname\":\"Beaulieu\"},{\"forename\":\"Matt\",\"surname\":\"Ratto\"},{\"forename\":\"Andrea\",\"surname\":\"Scharnhorst\"}]}],\"total_results\":66,\"total_pages\":4,\"current_page\":0,\"items_per_page\":20}";
            //debugLine = convertUnicode(debugLine);
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
