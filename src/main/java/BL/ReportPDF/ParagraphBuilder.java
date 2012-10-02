/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.ReportPDF;

import Controller.ControllerBean;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
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

    public static Paragraph getHeader() {
        Paragraph header = new Paragraph();
        header.setFont(HEADERFONT);
        header.setAlignment(Element.ALIGN_CENTER);
        header.add(new Chunk("Report for "));
        header.add(new Chunk(ControllerBean.getSearch().getFullname()));
        return header;
    }

    public static Paragraph getSubHeader() {
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

    public static Paragraph getCountPapers() {
        Paragraph subHeader = new Paragraph();
        subHeader.setFont(SUBHEADERFONT);
        subHeader.setAlignment(Element.ALIGN_LEFT);

        subHeader.add(new Chunk(ControllerBean.getSearch().getFullname()));
        subHeader.add(new Chunk(" has written "));
        subHeader.add(new Chunk(String.valueOf(ControllerBean.mendeleyDocs.getDocuments().size())));
        subHeader.add(new Chunk(" documents between "));
        subHeader.add(new Chunk(String.valueOf(ControllerBean.getMinYear())));
        subHeader.add(new Chunk(" and "));
        subHeader.add(new Chunk(String.valueOf(ControllerBean.getMaxYear())));
        subHeader.add(new Chunk(", with a total of "));
        subHeader.add(new Chunk(String.valueOf(ControllerBean.authorsInMendeleyDocs.size())));
        subHeader.add(new Chunk(" co-authors."));

        return subHeader;
    }
    
        public static Paragraph getMostFrequentCoAuthor() {
        Paragraph subHeader = new Paragraph();
        subHeader.setFont(SUBHEADERFONT);
        subHeader.setAlignment(Element.ALIGN_LEFT);

        subHeader.add(new Chunk("His (her) most frequent co-author is "));
        subHeader.add(new Chunk(ControllerBean.mostFrequentCoAuthor.getFullname()));
        subHeader.add(new Chunk(". They wrote "));
        subHeader.add(new Chunk(String.valueOf(ControllerBean.mostFrequentCoAuthor.getTimesMentioned())));
        subHeader.add(new Chunk(" documents together from "));
        subHeader.add(new Chunk(String.valueOf(ControllerBean.mostFrequentCoAuthor.getYearFirstCollab())));
        subHeader.add(new Chunk(" to "));
        subHeader.add(new Chunk(String.valueOf(ControllerBean.mostFrequentCoAuthor.getYearLastCollab())));
        subHeader.add(new Chunk("."));

        return subHeader;
    }

}
