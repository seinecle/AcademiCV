/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.ReportPDF;

import Controller.ControllerBean;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.faces.bean.ManagedProperty;
import javax.imageio.ImageIO;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import sun.misc.BASE64Decoder;

/**
 *
 * @author C. Levallois
 */
public class PDFCreator {

    private StreamedContent file;
    @ManagedProperty("#{controllerBean}")
    private ControllerBean controllerBean;

    public void setcontrollerBean(ControllerBean controllerBean) {
        this.controllerBean = controllerBean;
    }

    public StreamedContent getPDF(String dataURL) throws IOException, DocumentException {
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

        ParagraphBuilder pb = new ParagraphBuilder();
        document.add(pb.getHeader());
        document.add(pb.getSubHeader());
        document.add(imgIText);
        document.add(pb.getCountPapers());
        document.add(pb.getMostFrequentCoAuthor());

        document.close();
        docWriter.close();

        InputStream stream = new ByteArrayInputStream(baosPDF.toByteArray());
        file = new DefaultStreamedContent(stream, "application/pdf", "academicv " + controllerBean.getSearch().getFullname());

        return file;
    }
}
