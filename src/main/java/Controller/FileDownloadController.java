/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import BL.ReportPDF.ParagraphBuilder;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Utilities;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.imageio.ImageIO;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import sun.misc.BASE64Decoder;

/**
 *
 * @author C. Levallois
 */
@ManagedBean
@ViewScoped
public class FileDownloadController implements Serializable {

    private StreamedContent file;
    private String dataURL;

    public FileDownloadController() {
    }

    public String getDataURL() {
        return dataURL;
    }

    public void setDataURL(String dataURL) {
        this.dataURL = dataURL;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }

    public StreamedContent getFile() throws IOException, DocumentException {

        //GETS THE PIC OF THE CIRCLE
//        System.out.println("dataURL: " + dataURL);
//        ExternalContext external = FacesContext.getCurrentInstance().getExternalContext();
//        ServletContext servletContext = (ServletContext) external.getContext();
//        String filenameCircle = servletContext.getRealPath("cloud.png");
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] decodedBytes;
        decodedBytes = decoder.decodeBuffer(dataURL.split("^data:image/(png|jpeg);base64,")[1]);

        //TO SAVE THE CIRCLE JUST BY ITSELF
//        BufferedImage imag = ImageIO.read(in);
//        ImageIO.write(imag, "png", new File(filenameCircle));

        //CREATES PDF DOC
//        String filenamePDF = servletContext.getRealPath("report.pdf");

//        PDDocument documentBox = new PDDocument();
//        PDPage page = new PDPage();
//        documentBox.addPage(page);
//        PDFont font = PDType1Font.HELVETICA_BOLD;


        //WRITES TEXT IN THE DOC
//        contentStream.beginText();
//        contentStream.setFont(font, 12);
//        contentStream.moveTextPositionByAmount(100, 700);
//        contentStream.drawString("Hello World");
//        contentStream.endText();


        //PUTS CIRCLE IN THE PDF

        InputStream in = new ByteArrayInputStream(decodedBytes);
        BufferedImage pngBufferedImage = ImageIO.read(in);

        Document document = new Document();
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        PdfWriter docWriter;
        docWriter = PdfWriter.getInstance(document, baosPDF);

        document.open();
        Image imgIText = Image.getInstance(pngBufferedImage, null);
        imgIText.setAlignment(Element.ALIGN_CENTER);
//        imgIText.setAbsolutePosition(Utilities.millimetersToPoints(30f), Utilities.millimetersToPoints(170f));


        //this line are to resize the circle. The value of actualDpi can be modified.
        float actualDpi = 640;
        System.out.println("actual dpi: " + actualDpi);
        if (actualDpi > 0) {
            imgIText.scalePercent(72f / actualDpi * 100);
        }

        document.add(ParagraphBuilder.getHeader());
        document.add(ParagraphBuilder.getSubHeader());
        document.add(imgIText);
        document.add(ParagraphBuilder.getCountPapers());
        document.add(ParagraphBuilder.getMostFrequentCoAuthor());
        
        document.close();
        docWriter.close();

                InputStream 
        stream = new ByteArrayInputStream(baosPDF.toByteArray());
        file = new DefaultStreamedContent(stream, "application/pdf", "report on rings.pdf");


        return file;
    }
}
