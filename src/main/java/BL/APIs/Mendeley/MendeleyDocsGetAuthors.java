/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.APIs.Mendeley;

import Controller.ControllerBean;
import Model.Author;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author C. Levallois
 */
public class MendeleyDocsGetAuthors {

    public MendeleyDocsGetAuthors() {
    }

    public TreeSet<Author> countAuthors(List<MendeleyDoc> listDocs, String forename, String surname) {

        String search = surname + ", " + forename;
        String search2 = forename + " " + surname;
        ArrayList<Author> listAuthors = new ArrayList();
        TreeSet<Author> setAuthors = new TreeSet();
        TreeSet<Author> setAuthorsOriginal = new TreeSet();
        int minYear = 3000;
        int maxYear = 0;

        if (listDocs.isEmpty()) {
            System.out.println("list of MendeleyDocs is empty");
        }
        Iterator<MendeleyDoc> listDocsIterator = listDocs.iterator();
        Iterator<Author> setAuthorsOriginalIterator;
        List<Author> arrayAuthorsInCurrDoc;
        int docYearInteger;
        String docYearString;

        ControllerBean.nbMendeleyDocs = listDocs.size();


        while (listDocsIterator.hasNext()) {

            MendeleyDoc currDoc = listDocsIterator.next();

            if (currDoc.getAuthors() != null) {
                arrayAuthorsInCurrDoc = currDoc.getAuthors();
                docYearString = currDoc.getYear();
                if (docYearString != null && !docYearString.equals("")) {
                    docYearInteger = Integer.parseInt(docYearString);
                    minYear = Math.min(minYear,docYearInteger);
                    maxYear = Math.max(maxYear,docYearInteger);
                } else {
                    docYearInteger = -1;
                }
                for (Author coauthor : arrayAuthorsInCurrDoc) {
                    System.out.println("coauthor: \"" + coauthor.getFullnameWithComma() + "\"");
                    if (!StringUtils.stripAccents(coauthor.getFullname().toLowerCase().replaceAll("-", " ")).trim().equals(StringUtils.stripAccents(search2.toLowerCase().replaceAll("-", " ").trim()))) {
//                        if (coauthor.getForename().startsWith("Krz")) {
//                            System.out.println("Christophe found");
//                            System.out.println("YEAR IS: " + docYearInteger);
//                        }
                        coauthor.setYearFirstCollab(docYearInteger);
                        coauthor.setYearLastCollab(docYearInteger);
                        listAuthors.add(coauthor);
                        setAuthorsOriginal.add(coauthor);
                    }
                }
            }
        }

        setAuthorsOriginalIterator = setAuthorsOriginal.iterator();
        while (setAuthorsOriginalIterator.hasNext()) {
            Author element1 = setAuthorsOriginalIterator.next();
            int startYearElement1 = element1.getYearFirstCollab();
            int endYearElement1 = element1.getYearLastCollab();
            int count = 1;

            for (Author element2 : listAuthors) {
                if (element1.getFullnameWithComma().equals(element2.getFullnameWithComma())) {
//                    System.out.println("same element detected!");
                    count++;
                    int startYearElement2 = element2.getYearFirstCollab();
                    int endYearElement2 = element2.getYearLastCollab();

                    if (startYearElement2 < startYearElement1) {
                        startYearElement1 = startYearElement2;
                    }
                    if (endYearElement2 > endYearElement1) {
                        endYearElement1 = endYearElement2;
                    }
                }
            }
            element1.setYearFirstCollab(startYearElement1);
//            System.out.println("first Year Collab for " + element1.getFullname() + ": " + startYearElement1);
            element1.setYearLastCollab(endYearElement1);
//            System.out.println("first Year Collab for " + element1.getFullname() + ": " + endYearElement1);
            element1.setTimesMentioned(count - 1);
//            System.out.println("count for " + element1.getFullname() + ": " + count);
            setAuthors.add(element1);
            ControllerBean.setMinYear(minYear);
            ControllerBean.setMaxYear(maxYear);

        }
        return setAuthors;
    }
}
