/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.APIs.Arxiv;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author C. Levallois
 */
public class ArxivAPIresponseParser extends DefaultHandler {

    boolean newAuthor;
    String currAuthor;
    StringBuilder authorBuilder;
    private boolean newEntry;
    private boolean newTitle;
    private StringBuilder titleBuilder;
    private boolean newPublished;
    private StringBuilder publishedBuilder;

    private void doTheParsing(InputSource is) throws IOException {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse(is, this);

        } catch (SAXException se) {
        } catch (ParserConfigurationException pce) {
        } catch (IOException ie) {
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {



        if (qName.matches("entry")) {
            newEntry = true;
        }

        if (qName.matches("author")) {
            newAuthor = true;
            authorBuilder = new StringBuilder();
        }


        if (qName.matches("title")) {
            newTitle = true;
            titleBuilder = new StringBuilder();

        }

        if (qName.matches("published")) {
            newPublished = true;
            publishedBuilder = new StringBuilder();

        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        if (newTitle) {
            titleBuilder.append(ch, start, length);
        }

        if (newAuthor) {
            authorBuilder.append(ch, start, length);
        }

        if (newPublished) {
            publishedBuilder.append(ch, start, length);
        }
    }

    
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {


        if (qName.equalsIgnoreCase("author") & authorBuilder != null) {
            documentBuilder.append(abstractBuilder.toString()).append("|");
            //System.out.println("documentBuilder so far is: " + documentBuilder.toString());
            newAuthor = false;
        }

        if (qName.equalsIgnoreCase("title") & titleBuilder != null) {
            documentBuilder.append(titleBuilder.toString()).append("|");
            //System.out.println("documentBuilder so far is: " + documentBuilder.toString());
            newTitle = false;
        }

        if (qName.matches("published")) {
            //documentBuilder.append(attachmentBuilder.toString()).append("|");
            newPublished = false;

        }

        if (qName.matches("dcterms:isPartOf")) {
            newIsPartOf = false;
        }

        if (qName.matches("dc:date") & Mainthread.datesIncluded) {
            System.out.println("date is: " + dateBuilder.toString());
            documentBuilder.append(dateBuilder.toString().replaceAll("(.*)(\\d\\d\\d\\d)(.*)", "$2")).append("|");

            newDate = false;
        }


        if (qName.matches("dc:subject") & Mainthread.subjectsIncluded) {
//            if (subjectBuilder.toString().toLowerCase().contains("pepsi")) {
            System.out.println("subject is: " + subjectBuilder.toString());
            documentBuilder.append(subjectBuilder.toString()).append("|");
            //System.out.println("documentBuilder so far is: " + documentBuilder.toString());

//            }
            newSubject = false;
        }

        if (qName.matches("foaf:surname") & Mainthread.authorsIncluded) {
            System.out.println("author is: " + authorBuilder.toString());
            documentBuilder.append(authorBuilder.toString()).append("|");
            //System.out.println("documentBuilder so far is: " + documentBuilder.toString());

            newAuthor = false;
        }

        if (qName.contains("bib:authors") & !newIsPartOf & documentBuilder != null) {
            System.out.println("whole documentBuilder is: " + documentBuilder.toString());
            //pool.execute(new WorkerThread(documentBuilder.toString()));
            WorkerThread(documentBuilder.toString());
            newDocument = false;
        }



    }
}
}
