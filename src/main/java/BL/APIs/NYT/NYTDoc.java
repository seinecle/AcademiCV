/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.APIs.NYT;

/**
 *
 * @author C. Levallois
 */
public class NYTDoc {

    private String uuid;
    private String title;
    private String year;
    private String url;
    private String date;

    NYTDoc() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    
    

}

