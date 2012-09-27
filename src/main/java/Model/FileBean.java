package Model;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.commons.io.IOUtils;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author C. Levallois
 */
@ManagedBean
@ViewScoped
public class FileBean implements Serializable{

    private UploadedFile file;

    /**
     * Creates a new instance of FileBean
     */
    public FileBean() {
    }

    public UploadedFile getFile() {
        System.out.println("we get file");
        return file;
    }

    public void setFile(UploadedFile file) throws FileNotFoundException, IOException {
        System.out.println("we set file");
        this.file = file;

    }

    public void save() throws IOException {
        System.out.println("we save file");
        IOUtils.copy(file.getInputstream(), new FileOutputStream("D:\\" + file.getFileName()));
        BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputstream()));
        String currLine = br.readLine();
        Integer counterLines = 0;
        while (currLine != null && counterLines < 5) {
            System.out.println("currLine is: " + currLine);
            counterLines++;
        }

    }
}
