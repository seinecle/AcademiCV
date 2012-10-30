/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import BL.ReportPDF.ParagraphBuilder;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
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
public class FileDownloadController {

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
        InputStream in = new ByteArrayInputStream(decodedBytes);
        BufferedImage src = ImageIO.read(in);
        System.out.println("source width: " + src.getWidth());
        System.out.println("source height: " + src.getHeight());
        int FACTOR = 1;
        BufferedImage dest = new BufferedImage(src.getWidth() / FACTOR, src.getWidth() / FACTOR,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = dest.createGraphics();
        AffineTransform at = AffineTransform.getScaleInstance(
                (double) 1 / FACTOR,
                (double) 1 / FACTOR);
        g.drawRenderedImage(src, at);

        Document document = new Document();
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        PdfWriter docWriter;
        docWriter = PdfWriter.getInstance(document, baosPDF);

        document.open();
        Image imgIText = Image.getInstance(dest, null);
        imgIText.setAlignment(Element.ALIGN_CENTER);
//        imgIText.setAbsolutePosition(Utilities.millimetersToPoints(30f), Utilities.millimetersToPoints(170f));


        imgIText.scaleAbsolute(300, 300);
//        imgIText.setCompressionLevel(9);

        document.add(ParagraphBuilder.getHeader());
        document.add(ParagraphBuilder.getSubHeader());
        document.add(imgIText);
        document.add(ParagraphBuilder.getCountPapers());
        document.add(ParagraphBuilder.getMostFrequentCoAuthor());

        document.close();
        docWriter.close();

        InputStream stream = new ByteArrayInputStream(baosPDF.toByteArray());
        file = new DefaultStreamedContent(stream, "application/pdf", "academicv "+ControllerBean.getSearch().getFullname());


        return file;
    }
}
