/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import BL.APIs.NYT.NYTDoc;
import Controller.ControllerBean;
import Model.Author;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.joda.time.LocalDate;

/**
 *
 * @author C. Levallois
 */
@ManagedBean
@ViewScoped
public class ReportBean implements Serializable {

    private String json;
    private String nameClicked;
    private String countDocsCurrNameClicked;
    private Author authorClicked;

    public ReportBean() {
        this.json = ControllerBean.getJson();
        authorClicked = new Author("fake name");
        authorClicked.setYearFirstCollab(0);
        authorClicked.setYearLastCollab(3000);
        authorClicked.setTimesMentioned(666);
    }

    public String getJson() {
        return json;
    }

    public String getNameClicked() {
        return nameClicked;
    }

    public Author getAuthorClicked() {
        return authorClicked;
    }

    public void passName() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map map = context.getExternalContext().getRequestParameterMap();
        this.nameClicked = (String) map.get("nameClicked");
        this.countDocsCurrNameClicked = (String) map.get("countDocs");

        Iterator<Author> setAuthorsIterator = ControllerBean.setAuthors.iterator();
        Author currAuthor = null;
        while (setAuthorsIterator.hasNext()) {
            currAuthor = setAuthorsIterator.next();
            if (currAuthor.getFullnameWithComma().equals(nameClicked)) {
                break;
            }
        }
        authorClicked = currAuthor;

        FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().put("clickedAuthor", this.nameClicked);
        FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().put("uuid", ControllerBean.uuid.toString());
    }

    public Integer getCountDocsCurrNameClicked() {
        return authorClicked.getTimesMentioned();
    }

    public String obtainFullName() {
        return authorClicked.getFullname();
    }

    public String getNYTLaius() {
        StringBuilder sb = new StringBuilder();

        sb.append("<div style=\"font-size:120%;\">");
        sb.append("<b>");
        sb.append("Media presence</b><br>");
        sb.append("</div>");

        if (ControllerBean.NYTDocs.getDocuments().isEmpty()) {
            sb.append("No media presence was detected for ");
            sb.append(ControllerBean.getSearch().getFullname());
            sb.append(" (experimental feature).");

        } else {
            sb.append(ControllerBean.getSearch().getFullname());
            sb.append(" is mentioned in the following article(s) fron the New York Times:<br>");
            for (NYTDoc element : ControllerBean.NYTDocs.getDocuments()) {
                sb.append(element.getDate().substring(0, 4));
                sb.append(". \"<a href=\"");
                sb.append(element.getUrl());
                sb.append("\">");
                sb.append(element.getTitle());
                sb.append("</a>.\"");
                sb.append("<br>");
            }
        }

        sb.append("<p></p>");
        return sb.toString();
    }

    public String getClickedAuthorLaius() {
        StringBuilder sb = new StringBuilder();
        sb.append("They have ");
        if (authorClicked.getTimesMentioned() == 1) {
            sb.append("one shared publication, in ");
            sb.append(authorClicked.getYearFirstCollab());
        } else {
            sb.append(authorClicked.getTimesMentioned());
            sb.append(" shared publications");
            if (authorClicked.getYearFirstCollab() == authorClicked.getYearLastCollab()) {
                if (authorClicked.getTimesMentioned() == 2) {
                    sb.append(", both in ");
                } else {
                    sb.append(", all in ");
                }
                sb.append(authorClicked.getYearLastCollab());
            } else {
                sb.append(", from ");
                sb.append(authorClicked.getYearFirstCollab());
                sb.append(" to ");
                sb.append(authorClicked.getYearLastCollab());
            }
        }
        sb.append(".");
        return sb.toString();

    }

    static public String getGeneralLaius() {
        StringBuilder toReturn = new StringBuilder();
        toReturn.append("This researcher has written ");
        toReturn.append(ControllerBean.nbDocs);
        if (ControllerBean.nbDocs == 1) {
            toReturn.append(" document in ");
            toReturn.append(ControllerBean.minYear);

        } else if (ControllerBean.minYear == ControllerBean.maxYear) {
            toReturn.append(" documents in ");
            toReturn.append(ControllerBean.minYear);
        } else {
            toReturn.append(" documents between ");
            toReturn.append(ControllerBean.minYear);
            toReturn.append(" and ");
            toReturn.append(ControllerBean.maxYear);
        }
        if (ControllerBean.setAuthors.size() > 0) {
            toReturn.append(", with a total of ");
            toReturn.append(ControllerBean.setAuthors.size());
            toReturn.append(" co-authors");
        }
        toReturn.append(".");


        return toReturn.toString();
    }

    static public String getAge() {
        StringBuilder sb = new StringBuilder();
        if (ControllerBean.getSearch().getBirthYear() != null && ControllerBean.getSearch().getBirthYear() != 0) {
            LocalDate today = new LocalDate();
            Integer currYear = today.getYear() - ControllerBean.getSearch().getBirthYear();
            sb.append(ControllerBean.getSearch().getFullname());
            sb.append(" is ").append(currYear).append(" years old.");
            sb.append("<p></p>");
        }

        return sb.toString();

    }

    static public String getMostFrequentSource() {
        StringBuilder toReturn = new StringBuilder();
        int countTitle = ControllerBean.getMostFreqSource().getRight();
        if (countTitle == 0) {
            return toReturn.toString();
        }
        toReturn.append("<div style=\"font-size:120%;\">");

        toReturn.append("<b>Journal with most publications by this researcher</b><br>");
        toReturn.append("</div>");

        toReturn.append(ControllerBean.getMostFreqSource().getLeft());
        toReturn.append(" (");
        toReturn.append(countTitle);
        if (countTitle == 1) {
            toReturn.append(" document published in this outlet).");
        } else {
            toReturn.append(" documents published in this outlet).");
        }

        toReturn.append("<p></p>");
        return toReturn.toString();
    }

    static public String getMostFrequentCoAuthors() {
        HashSet<Author> mostFrequentCoAuthors = ControllerBean.mostFrequentCoAuthors;
        StringBuilder toReturn = new StringBuilder();

        if (mostFrequentCoAuthors == null) {
            return toReturn.toString();
        }

        toReturn.append("<div style=\"font-size:120%;\">");
        toReturn.append("<b>Most frequent co-author(s)</b><b><br>");
        toReturn.append("</div>");


        if (mostFrequentCoAuthors.size() == 1) {
            Author mostFrequentCoAuthor = mostFrequentCoAuthors.iterator().next();
            int nbCollab = mostFrequentCoAuthor.getTimesMentioned();
            toReturn.append(mostFrequentCoAuthor.getFullname());
            toReturn.append("</b>");
            toReturn.append(".<br>");
            toReturn.append("Together, they have written ");
            toReturn.append(mostFrequentCoAuthor.getTimesMentioned());
            if (nbCollab == 1) {
                toReturn.append(" document");
            } else {
                toReturn.append(" documents");
            }
            if (ControllerBean.minYear == ControllerBean.maxYear) {
                toReturn.append(" in  ");
                toReturn.append(mostFrequentCoAuthor.getYearFirstCollab());
            } else {
                toReturn.append(" from ");
                toReturn.append(mostFrequentCoAuthor.getYearFirstCollab());
                toReturn.append(" to ");
                toReturn.append(mostFrequentCoAuthor.getYearLastCollab());
                toReturn.append(".");
            }

        } else {
            Iterator<Author> mostFrequentCoAuthorsIterator = mostFrequentCoAuthors.iterator();
            Author currAuthor;
            int maxNbCollab = 0;
            while (mostFrequentCoAuthorsIterator.hasNext()) {
                currAuthor = mostFrequentCoAuthorsIterator.next();
                maxNbCollab = Math.max(currAuthor.getTimesMentioned(), maxNbCollab);
            }
            if (maxNbCollab == 1) {
                toReturn.append("- multiple coauthors, did not write more than one paper with the same coauthor.");

            } else {
                mostFrequentCoAuthorsIterator = mostFrequentCoAuthors.iterator();
                while (mostFrequentCoAuthorsIterator.hasNext()) {
                    currAuthor = mostFrequentCoAuthorsIterator.next();
                    toReturn.append("- <b>");
                    toReturn.append(currAuthor.getFullname());
                    toReturn.append("</b>, with ");
                    toReturn.append(currAuthor.getTimesMentioned());
                    if (currAuthor.getYearFirstCollab() == currAuthor.getYearLastCollab()) {
                        toReturn.append(" shared publications in ");
                        toReturn.append(currAuthor.getYearFirstCollab());
                    } else {
                        toReturn.append(" shared publications from ");
                        toReturn.append(currAuthor.getYearFirstCollab());
                        toReturn.append(" to ");
                        toReturn.append(currAuthor.getYearLastCollab());
                        toReturn.append(".<br>");

                    }
                }
            }

        }
        toReturn.append("<p></p>");
        return toReturn.toString();
    }
}
