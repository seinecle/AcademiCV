/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import BL.ReportPDF.PDFCreator;
import com.itextpdf.text.DocumentException;
import java.io.IOException;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author C. Levallois
 */
@ManagedBean
@RequestScoped
public class FileDownloadController implements Serializable {

    private String dataURL;
    private StreamedContent file;

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

        file = PDFCreator.getPDF(dataURL);
        return file;
    }
}
