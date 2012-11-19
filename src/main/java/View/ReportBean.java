/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Controller.ControllerBean;
import Model.Author;
import Model.Document;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
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
    @ManagedProperty("#{controllerBean}")
    private ControllerBean controllerBean;

    public void setcontrollerBean(ControllerBean controllerBean) {
        this.controllerBean = controllerBean;
    }

    public ReportBean() {
    }

    @PostConstruct
    private void init() {
        this.json = controllerBean.getJson();
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

        Iterator<Author> setAuthorsIterator = controllerBean.getSetAuthors().iterator();
        Author currAuthor = null;
        while (setAuthorsIterator.hasNext()) {
            currAuthor = setAuthorsIterator.next();
            if (currAuthor.getFullnameWithComma().equals(nameClicked)) {
                break;
            }
        }
        authorClicked = currAuthor;

        FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().put("clickedAuthor", this.nameClicked);
        FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().put("uuid", controllerBean.uuid.toString());
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

        if (!controllerBean.isNYTfound()) {
            sb.append("No media presence was detected for ");
            sb.append(controllerBean.getSearch().getFullname());
            sb.append(" (experimental feature).");

        } else {
            sb.append(controllerBean.getSearch().getFullname());
            sb.append(" is mentioned in the following article(s) fron the New York Times:<br>");
            for (Document element : controllerBean.getSetMediaDocs()) {
                if (!element.getPublication_outlet().equals("New York Times")) {
                    continue;
                }
                sb.append(element.getYear());
                sb.append(". \"<a href=\"");
                sb.append(element.getNyt_url());
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

    public String getGeneralLaius() {
        StringBuilder toReturn = new StringBuilder();
        toReturn.append("This researcher has written ");
        toReturn.append(controllerBean.getSetDocs().size());
        if (controllerBean.getSetDocs().size() == 1) {
            toReturn.append(" document in ");
            toReturn.append(controllerBean.getSearch().getYearFirstCollab());

        } else if (controllerBean.getSearch().getYearFirstCollab() == controllerBean.getSearch().getYearLastCollab()) {
            toReturn.append(" documents in ");
            toReturn.append(controllerBean.getSearch().getYearFirstCollab());
        } else {
            toReturn.append(" documents between ");
            toReturn.append(controllerBean.getSearch().getYearFirstCollab());
            toReturn.append(" and ");
            toReturn.append(controllerBean.getSearch().getYearLastCollab());
        }
        if (controllerBean.getSetDocs().size() > 0) {
            toReturn.append(", with a total of ");
            toReturn.append(controllerBean.getSetDocs().size());
            toReturn.append(" co-authors");
        }
        toReturn.append(".");


        return toReturn.toString();
    }

    public String getAge() {
        StringBuilder sb = new StringBuilder();
        if (controllerBean.getSearch().getBirthYear() != null && controllerBean.getSearch().getBirthYear() != 0) {
            LocalDate today = new LocalDate();
            Integer currYear = today.getYear() - controllerBean.getSearch().getBirthYear();
            sb.append(controllerBean.getSearch().getFullname());
            sb.append(" is ").append(currYear).append(" years old.");
            sb.append("<p></p>");
        }

        return sb.toString();

    }

    public String getMostFrequentSource() {
        StringBuilder toReturn = new StringBuilder();
        int countTitle = controllerBean.getSearch().getMostFrequentOutlet().getRight();
        if (countTitle == 0) {
            return toReturn.toString();
        }
        toReturn.append("<div style=\"font-size:120%;\">");

        toReturn.append("<b>Journal with most publications by this researcher</b><br>");
        toReturn.append("</div>");

        toReturn.append(controllerBean.getSearch().getMostFrequentOutlet().getLeft());
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

    public String getMostFrequentCoAuthors() {
        Set<Author> mostFrequentCoAuthors = controllerBean.getSearch().getSetMostFrequentCoAuthors();
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
            if (controllerBean.getSearch().getYearFirstCollab() == controllerBean.getSearch().getYearLastCollab()) {
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
