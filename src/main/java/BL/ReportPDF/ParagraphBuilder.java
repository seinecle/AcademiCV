/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.ReportPDF;

import Model.Author;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import java.util.Iterator;
import java.util.Set;
import org.joda.time.DateTime;

/**
 *
 * @author C. Levallois
 */
public class ParagraphBuilder {

    public static final Font HEADERFONT =
            new Font(FontFamily.TIMES_ROMAN,
            20);
    public static final Font SUBHEADERFONT =
            new Font(FontFamily.TIMES_ROMAN,
            12);
    public static final Font TEXTNORMALSIZE =
            new Font(FontFamily.TIMES_ROMAN,
            12);
    
    private Author search;

    public ParagraphBuilder(Author search) {
    
        this.search = search;
    
    }

    public Paragraph getHeader() {
        Paragraph header = new Paragraph();
        header.setFont(HEADERFONT);
        header.setAlignment(Element.ALIGN_CENTER);
        header.add(new Chunk("AcademiCV for "));
        header.add(new Chunk(search.getFullname()));
        return header;
    }

    public Paragraph getSubHeader() {
        Paragraph subHeader = new Paragraph();
        subHeader.setFont(TEXTNORMALSIZE);
        subHeader.setAlignment(Element.ALIGN_CENTER);

        subHeader.add(new Chunk("(generated "));
        subHeader.add(new Chunk(String.valueOf(new DateTime().getDayOfMonth())));
        subHeader.add(new Chunk(" "));
        subHeader.add(new Chunk(String.valueOf(new DateTime().monthOfYear().getAsText())));
        subHeader.add(new Chunk(" "));
        subHeader.add(new Chunk(String.valueOf(new DateTime().getYear())));
        subHeader.add(new Chunk(")"));
        return subHeader;
    }

    public Paragraph getCountPapers() {
        Paragraph subHeader = new Paragraph();
        subHeader.setFont(SUBHEADERFONT);
        subHeader.setAlignment(Element.ALIGN_LEFT);

        subHeader.add(new Chunk(search.getFullname()));
        subHeader.add(new Chunk(" has written "));
        subHeader.add(new Chunk(String.valueOf(search.getNumberOfDocs())));
        subHeader.add(new Chunk(" documents between "));
        subHeader.add(new Chunk(String.valueOf(search.getYearFirstCollab())));
        subHeader.add(new Chunk(" and "));
        subHeader.add(new Chunk(String.valueOf(search.getYearLastCollab())));
        subHeader.add(new Chunk(", with a total of "));
        subHeader.add(new Chunk(String.valueOf(search.getNumberCoAuthors())));
        subHeader.add(new Chunk(" co-authors."));

        return subHeader;
    }

    public Paragraph getIdentity() {
        Paragraph identity = new Paragraph();
        identity.setFont(SUBHEADERFONT);
        identity.setAlignment(Element.ALIGN_LEFT);
        identity.add(new Chunk("Last known affiliation: "));
        identity.add(new Chunk(search.getMostRecentAffiliation()));
        identity.add(new Chunk("."));

        return identity;
    }

    public Paragraph getMostFrequentCoAuthor() {
        Paragraph subHeader = new Paragraph();
        subHeader.setFont(SUBHEADERFONT);
        subHeader.setAlignment(Element.ALIGN_LEFT);

        subHeader.add(new Chunk(getMostFrequentCoAuthors()));

        return subHeader;
    }

    public String getMostFrequentCoAuthors() {
        Set<Author> mostFrequentCoAuthors = search.getSetMostFrequentCoAuthors();
        StringBuilder toReturn = new StringBuilder();
        if (mostFrequentCoAuthors.size() == 1) {
            Author mostFrequentCoAuthor = mostFrequentCoAuthors.iterator().next();
            int nbCollab = mostFrequentCoAuthor.getTimesMentioned();
            toReturn.append("His or her most frequent co-author is ").append(mostFrequentCoAuthor.getFullname());
            toReturn.append("\n\n");
            toReturn.append("Together, they wrote ");
            toReturn.append(mostFrequentCoAuthor.getTimesMentioned());
            if (nbCollab == 1) {
                toReturn.append(" document");
            } else {
                toReturn.append(" documents");
            }
            if (nbCollab == 1) {
                toReturn.append(" in  ");
                toReturn.append(mostFrequentCoAuthor.getYearFirstCollab());
            } else {
                toReturn.append(" in the years from ");
                toReturn.append(mostFrequentCoAuthor.getYearFirstCollab());
                toReturn.append(" to ");
                toReturn.append(mostFrequentCoAuthor.getYearLastCollab());
            }
            toReturn.append(".\n");

        } else {
            Iterator<Author> mostFrequentCoAuthorsIterator = mostFrequentCoAuthors.iterator();
            Author currAuthor;
            int maxNbCollab = 0;
            while (mostFrequentCoAuthorsIterator.hasNext()) {
                currAuthor = mostFrequentCoAuthorsIterator.next();
                maxNbCollab = Math.max(currAuthor.getTimesMentioned(), maxNbCollab);
            }
            if (maxNbCollab == 1) {
                toReturn.append("This researcher has multiple coauthors, and never wrote more than one paper with the same coauthor.\n");
            } else {
                toReturn.append("This researcher's most frequent co-authors are:\n");
                mostFrequentCoAuthorsIterator = mostFrequentCoAuthors.iterator();
                while (mostFrequentCoAuthorsIterator.hasNext()) {
                    currAuthor = mostFrequentCoAuthorsIterator.next();
                    toReturn.append("- ");
                    toReturn.append(currAuthor.getFullname());
                    toReturn.append(", with ");
                    toReturn.append(currAuthor.getTimesMentioned());
                    toReturn.append(" shared publications from ");
                    toReturn.append(currAuthor.getYearFirstCollab());
                    toReturn.append(" to ");
                    toReturn.append(currAuthor.getYearLastCollab());
                    toReturn.append(".\n");
                    System.out.println("toReturn: " + toReturn.toString());
                }
            }
        }
        return toReturn.toString();
    }
}