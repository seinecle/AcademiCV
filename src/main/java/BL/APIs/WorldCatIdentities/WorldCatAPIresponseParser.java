/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.APIs.WorldCatIdentities;

import Controller.ControllerBean;
import Model.Author;
import View.ProgressBarMessenger;
import Utils.RemoveNonASCII;
import java.io.IOException;
import java.util.HashSet;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author C. Levallois
 */
public class WorldCatAPIresponseParser extends DefaultHandler {

    private boolean newNameType;
    private boolean newMatch;
    private boolean newURI;
    private boolean newEstablishedForm;
    private StringBuilder URIBuilder;
    private StringBuilder nameTypeBuilder;
    private StringBuilder establishedFormBuilder;
    private String currURI;
    private String currNameType;
    private String currEstablishedForm;
    private InputSource is;
    private boolean yearFound;
    private String currBirthYear;
    private String currSearchName;
    HashSet<String> setIdentitiesFound;
    private Author search;

    public WorldCatAPIresponseParser(InputSource newIs, Author search) {
        this.is = newIs;
        this.search  =search;
        currSearchName = search.getFullname();
        currSearchName = StringUtils.stripAccents(currSearchName);
        currSearchName = RemoveNonASCII.remove(currSearchName);
        setIdentitiesFound = new HashSet();

    }

    public HashSet<String> parse() throws IOException {



        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            System.out.println("starting to parse the worldcat API response");
            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse(is, this);

            System.out.println("finished parsing the worldcat API response");


        } catch (SAXException se) {
            System.out.println("SAXException: " + se);
        } catch (ParserConfigurationException pce) {
            System.out.println("ParserConfigurationException: " + pce);
        } catch (IOException ie) {
            System.out.println("IOException: " + ie);
        }

        return setIdentitiesFound;


    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        //attributes are the stuff following the name of the xml tag, such as example = "value
        //they are indexed, starting at 0
        // their value can be tested with attributes.getValue(indexNumber);

        if (qName.equals("match") && (attributes.getValue(0).equals("ExactMatches") | attributes.getValue(0).equals("FuzzyFirstName"))) {
            System.out.println("new Match found");
            newMatch = true;
        }

        if (qName.equals("nameType")) {
            newNameType = true;
            nameTypeBuilder = new StringBuilder();
        }

        if (qName.equals("establishedForm")) {
            newEstablishedForm = true;
            establishedFormBuilder = new StringBuilder();
            System.out.println("start of established form");
        }


        if (qName.equals("uri")) {
            newURI = true;
            URIBuilder = new StringBuilder();
            System.out.println("uri found");

        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        if (newURI) {
            URIBuilder.append(ch, start, length);
        }

        if (newNameType) {
            nameTypeBuilder.append(ch, start, length);
        }

        if (newEstablishedForm) {
            establishedFormBuilder.append(ch, start, length);
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {


        //case when an affiliation is provided with the author
        if (qName.equals("match") && newMatch && currNameType.equals("personal") && currURI != null) {

            yearFound = currURI.matches(".*\\(\\d\\d\\d\\d.*");
            if (yearFound) {
//                Author currMainSearchAuthor = ControllerBean.getSearch();
//                currMainSearchAuthor.setBirthYear(Integer.parseInt(currEstablishedForm.replaceAll(".*(\\(\\d\\d\\d\\d).*", "$1").substring(1)));
//                ControllerBean.setSearch(currMainSearchAuthor);
            } else {
                currBirthYear = null;
            }
            currURI = null;
            currNameType = null;
            newMatch = false;
        }

        if (qName.equals("uri") && newMatch) {
            currURI = URIBuilder.toString();
            if (!currURI.contains("lccn")) {
//            currURI = RemoveNonASCII.remove(currURI);
                currURI = currURI.toLowerCase();
                String[] termsInURI = currURI.split(",");
                termsInURI[0] = termsInURI[0].replace("/identities/np-", "");

                String URIreconstructed;
                if (termsInURI.length > 1) {
                    if (termsInURI[1].matches(".*\\$\\d\\d\\d\\d.*")) {
                        termsInURI[1] = termsInURI[1].replaceAll("(.*)(\\$\\d\\d\\d\\d)(.*)", "$1");
                    }
                    if (!termsInURI[1].equals(StringUtils.stripAccents(search.getForename()))) {
                        termsInURI[1] = "do not include in the set!";
                    }

                    if (!termsInURI[0].equals(StringUtils.stripAccents(search.getSurname()))) {
                        termsInURI[0] = "do not include in the set!";
                    }

                    URIreconstructed = termsInURI[1].trim() + "%20" + termsInURI[0].trim();
                } else {
                    URIreconstructed = "do not include in the set!";
                }
                URIreconstructed = URIreconstructed.replaceAll(" ", "%20");
                System.out.println("currURI = " + currURI);
                System.out.println("URIreconstructed = " + URIreconstructed);
                currSearchName = currSearchName.replaceAll(" ", "%20").toLowerCase();
                System.out.println("currSearchName: " + currSearchName);

                if (!URIreconstructed.contains(currSearchName)) {
                    currURI = null;
                } else {
                    System.out.println("uri added to the set:" + currURI);
                    currURI = currURI.replaceAll(" ", "%20");
                    setIdentitiesFound.add(currURI);
                }
            } else {
                setIdentitiesFound.add(currURI);
            }
            newURI = false;
        }

        if (qName.equals("nameType") && newMatch) {
            currNameType = nameTypeBuilder.toString();
            newNameType = false;
        }

        if (qName.equals("establishedForm") && newMatch) {
            currEstablishedForm = establishedFormBuilder.toString();
            newEstablishedForm = false;
        }
    }
}
