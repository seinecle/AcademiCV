/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.APIs.WorldCatIdentities;

import Controller.ControllerBean;
import View.ProgressBarMessenger;
import Model.Author;
import Model.Document;
import Utils.RemoveNonASCII;
import java.io.IOException;
import java.util.HashSet;
import javax.faces.bean.ManagedProperty;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author C. Levallois
 */
public class WorldCatAPIIdentityresponseParser extends DefaultHandler {

    private boolean newTitle;
    private boolean newCitation;
    private boolean newDate;
    private boolean newIsFiction;
    private boolean newSummary;
    private boolean newSub;
    private boolean newCreator;
    private boolean newAuthorityInfo;
    private boolean newNameInfo;
    private StringBuilder titleBuilder;
    private StringBuilder dateBuilder;
    private StringBuilder summaryBuilder;
    private StringBuilder isFictionBuilder;
    private StringBuilder subBuilder;
    private StringBuilder creatorBuilder;
    private String currTitle;
    private String currDate;
    private String currIsFiction;
    private String currSummary;
    private String currSub;
    private String currLanguage;
    private String currCreator;
    private String currSearchName;
    private Document currDoc;
    private HashSet<Author> currSetAuthors;
    private boolean yearFound;
    private InputSource is;
    private Author currAuthor;
    @ManagedProperty("#{controllerBean}")
    private ControllerBean controllerBean;

    public void setcontrollerBean(ControllerBean controllerBean) {
        this.controllerBean = controllerBean;
    }

    public WorldCatAPIIdentityresponseParser(InputSource newIs, Author search) {
        this.is = newIs;
        currSearchName = search.getFullnameWithComma();
        currSearchName = StringUtils.stripAccents(currSearchName);
        currSearchName = RemoveNonASCII.remove(currSearchName);

    }

    public void parse() throws IOException {



        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            System.out.println("starting to parse the worldcat API IDENTITY response");
            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse(is, this);

            System.out.println("finished parsing the worldcat API IDENTITY response");


        } catch (SAXException se) {
            System.out.println("SAXException: " + se);
        } catch (ParserConfigurationException pce) {
            System.out.println("ParserConfigurationException: " + pce);
        } catch (IOException ie) {
            System.out.println("IOException: " + ie);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        //attributes are the stuff following the name of the xml tag, such as example = "value
        //they are indexed, starting at 0
        // their value can be tested with attributes.getValue(indexNumber);

        if (qName.equals("citation")) {
            newCitation = true;
            currSetAuthors = new HashSet();
        }

        if (qName.equals("isFiction")) {
            newIsFiction = true;
            isFictionBuilder = new StringBuilder();
        }

        if (qName.equals("date")) {
            newDate = true;
            dateBuilder = new StringBuilder();
        }

        if (qName.equals("authorityInfo")) {
            newAuthorityInfo = true;
        }

        if (qName.equals("nameInfo")) {
            newNameInfo = true;
        }

        if (qName.equals("creator")) {
            newCreator = true;
            creatorBuilder = new StringBuilder();
        }

        if (qName.startsWith("sub")) {
            newSub = true;
            System.out.println("start of sub");

            subBuilder = new StringBuilder();
        }

        if (qName.equals("lang")) {
            currLanguage = attributes.getValue(0);
        }

        if (qName.equals("title")) {
            newTitle = true;
            titleBuilder = new StringBuilder();
        }

        if (qName.equals("summary")) {
            newSummary = true;
            summaryBuilder = new StringBuilder();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        if (newIsFiction) {
            isFictionBuilder.append(ch, start, length);
        }

        if (newDate) {
            dateBuilder.append(ch, start, length);
        }

        if (newSub) {
            subBuilder.append(ch, start, length);
        }

        if (newCreator) {
            creatorBuilder.append(ch, start, length);
        }

        if (newTitle) {
            titleBuilder.append(ch, start, length);
        }

        if (newSummary) {
            summaryBuilder.append(ch, start, length);
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {


        //case when an affiliation is provided with the author
        if (qName.equals("citation")) {
            if (currSub != null) {
                System.out.println("currSub analized at end of citation...");
                yearFound = currSub.matches(".*\\d\\d\\d\\d.*");
                if (yearFound) {
                    Author currMainSearchAuthor = controllerBean.getSearch();
                    currMainSearchAuthor.setBirthYear((Integer.parseInt(currSub.replaceFirst(".*(\\d\\d\\d\\d).*", "$1"))));
                    controllerBean.setSearch(currMainSearchAuthor);
                } else {
                    //nothing
                }
            }
            currDoc = new Document();
            currDoc.setTitle(currTitle);
            currDoc.setAuthors(currSetAuthors);
            if (currDate != null) {
                try {
                    currDoc.setYear(Integer.parseInt(currDate));
                } catch (java.lang.NumberFormatException e) {
                }
            }
            currDoc.setSummary(currSummary);
            currDoc.setLanguage(currLanguage);
            System.out.println("document added to the set");
            System.out.println("(title is:");
            System.out.println(currDoc.getTitle());
            if (currDoc.getYear() != null & currDoc.getTitle() != null & currDoc.getAuthors() != null) {
                controllerBean.addToSetDocs(currDoc);
            }


            currTitle = null;
            currIsFiction = null;
            currDate = null;
            currLanguage = null;
            currSummary = null;
            currSub = null;
            newCitation = false;


        }

        if (qName.equals("date") && newCitation) {
            currDate = dateBuilder.toString();
            newDate = false;
        }

        if (qName.equals("isFiction") && newCitation) {
            currIsFiction = isFictionBuilder.toString();
            newIsFiction = false;
        }

        if (qName.startsWith("sub") && (newCitation || newNameInfo || newAuthorityInfo)) {
            currSub = subBuilder.toString();
            newSub = false;
        }

        if (qName.equals("title") && newCitation) {
            currTitle = titleBuilder.toString();
            newTitle = false;
        }

        if (qName.equals("creator") && newCitation) {
            currCreator = creatorBuilder.toString();
            if (currCreator.contains(",")) {
                currAuthor = new Author();
                currAuthor.setFullnameWithComma(currCreator);
                currSetAuthors.add(currAuthor);
            }
            newCreator = false;
        }

        if (qName.equals("summary") && newCitation) {
            currSummary = summaryBuilder.toString();
            newSummary = false;
        }

        if (qName.equals("authorityInfo")) {
            if (currSub != null) {
                System.out.println("currSub analized at end of citation...");
                yearFound = currSub.matches(".*\\d\\d\\d\\d.*");
                if (yearFound) {
                    Author currMainSearchAuthor = controllerBean.getSearch();
                    currMainSearchAuthor.setBirthYear((Integer.parseInt(currSub.replaceFirst(".*(\\d\\d\\d\\d).*", "$1"))));
                    controllerBean.setSearch(currMainSearchAuthor);
                } else {
                    //nothing
                }
            }


            newAuthorityInfo = false;
        }

        if (qName.equals("nameInfo")) {
            if (currSub != null) {
                System.out.println("currSub analized at end of citation...");
                yearFound = currSub.matches(".*\\d\\d\\d\\d.*");
                if (yearFound) {
//                    Author currMainSearchAuthor = ControllerBean.getSearch();
//                    currMainSearchAuthor.setBirthYear((Integer.parseInt(currSub.replaceFirst(".*(\\d\\d\\d\\d).*", "$1"))));
//                    ControllerBean.setSearch(currMainSearchAuthor);
                } else {
                    //nothing
                }
            }

            newNameInfo = false;
        }

    }
}
