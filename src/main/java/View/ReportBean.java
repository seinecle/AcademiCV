/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Controller.ControllerBean;
import Model.Author;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

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

        toReturn.append(", with a total of ");
        toReturn.append(ControllerBean.setAuthors.size());
        toReturn.append(" co-authors.");





        return toReturn.toString();
    }

    static public String getMostFrequentCoAuthors() {
        HashSet<Author> mostFrequentCoAuthors = ControllerBean.mostFrequentCoAuthors;
        StringBuilder toReturn = new StringBuilder();
        if (mostFrequentCoAuthors.size() == 1) {
            Author mostFrequentCoAuthor = mostFrequentCoAuthors.iterator().next();
            int nbCollab = mostFrequentCoAuthor.getTimesMentioned();
            toReturn.append("<p>His or her most frequent co-author is <b>" + mostFrequentCoAuthor.getFullname());
            toReturn.append("</b>.<br></br>\n");
            toReturn.append("Together, they wrote ");
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
                toReturn.append(" in the years from ");
                toReturn.append(mostFrequentCoAuthor.getYearFirstCollab());
                toReturn.append(" to ");
                toReturn.append(mostFrequentCoAuthor.getYearLastCollab());
            }
            toReturn.append(".</p >");

        } else {
            Iterator<Author> mostFrequentCoAuthorsIterator = mostFrequentCoAuthors.iterator();
            Author currAuthor;
            int maxNbCollab = 0;
            while (mostFrequentCoAuthorsIterator.hasNext()) {
                currAuthor = mostFrequentCoAuthorsIterator.next();
                maxNbCollab = Math.max(currAuthor.getTimesMentioned(), maxNbCollab);
            }
            if (maxNbCollab == 1) {
                toReturn.append("<p>He or she has multiple coauthors, and never wrote more than one paper with the same coauthor.</p>");
            } else {
                toReturn.append("<p>His or her most frequent co-authors are:</p>");
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
                    }
                    toReturn.append(". <br>");
                    System.out.println("toReturn: " + toReturn.toString());

                }
            }

        }
        return toReturn.toString();
    }
}
