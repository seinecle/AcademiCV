/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BL.APIs.Arxiv;

import Controller.ControllerBean;
import Model.Affiliation;
import Model.Author;
import Model.Document;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.faces.bean.ManagedProperty;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author C. Levallois
 */
public class ArxivAPIresponseParser extends DefaultHandler {

    private HashMap<String, String> mapPrimaryCategories;
    private boolean newAuthor;
    private boolean newEntry;
    private boolean newTitle;
    private boolean newAffiliation;
    private boolean newYearPublished;
    private boolean newPrimaryCategory;
    private StringBuilder titleBuilder;
    private StringBuilder yearPublishedBuilder;
    private StringBuilder affiliationBuilder;
    private StringBuilder authorBuilder;
    private StringBuilder primaryCategoryBuilder;
    private HashSet<Author> currDocSetAuthors;
    private String currDocTitle;
    private Integer currDocYearPublished;
    private String currDocPrimaryCategory;
    private String currDocCurrAuthorAffiliation;
    private Document currDocument;
    private Author currAuthor;
    private Affiliation currAffiliation;
    private HashSet<Affiliation> currSetAffiliations;
    private InputSource is;
    private int nbArxivDocs = 0;
    private Set<Document> setArxivDocs = new HashSet();

    public ArxivAPIresponseParser(InputSource newIs) {
        this.is = newIs;
    }

    public Set<Document> parse() throws IOException {

        //populate map of arxiv primary categories
        populateMapPrimaryCategories();

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse(is, this);


        } catch (SAXException se) {
            System.out.println("SAXException: " + se);
        } catch (ParserConfigurationException pce) {
            System.out.println("ParserConfigurationException: " + pce);
        } catch (IOException ie) {
            System.out.println("IOException: " + ie);
        }
        
        return setArxivDocs;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {



        if (qName.matches("entry")) {
            newEntry = true;
            currDocument = new Document();
            currDocSetAuthors = new HashSet();
        }

        if (qName.matches("author")) {
            currDocCurrAuthorAffiliation = null;
            newAuthor = true;
            authorBuilder = new StringBuilder();
        }


        if (qName.matches("title")) {
            newTitle = true;
            titleBuilder = new StringBuilder();
        }

        if (qName.matches("published")) {
            newYearPublished = true;
            yearPublishedBuilder = new StringBuilder();
        }

        if (qName.matches("arxiv:affiliation")) {
            newAffiliation = true;
            affiliationBuilder = new StringBuilder();
        }

        if (qName.matches("arxiv:primary_category")) {
            newPrimaryCategory = true;
            primaryCategoryBuilder = new StringBuilder();
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        if (newTitle) {
            titleBuilder.append(ch, start, length);
        }

        if (newAuthor & !newAffiliation) {
            authorBuilder.append(ch, start, length);
        }

        if (newYearPublished) {
            yearPublishedBuilder.append(ch, start, length);
        }

        if (newAffiliation) {
            affiliationBuilder.append(ch, start, length);
        }

        if (newPrimaryCategory) {
            primaryCategoryBuilder.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {


        //case when an affiliation is provided with the author
        if (qName.equalsIgnoreCase("author") & authorBuilder != null & currDocCurrAuthorAffiliation != null) {
            currAuthor = new Author(authorBuilder.toString());
//            System.out.println("affiliation detected:");
//            System.out.println(currDocCurrAuthorAffiliation);
//            System.out.println(currDocYearPublished);
            currAffiliation = new Affiliation(currDocCurrAuthorAffiliation);
            currAffiliation.setYear(currDocYearPublished);
            currSetAffiliations = new HashSet();
            currSetAffiliations.add(currAffiliation);
            currAuthor.setSetAffiliations(currSetAffiliations);
            currDocSetAuthors.add(currAuthor);
            currDocCurrAuthorAffiliation = null;
            currAffiliation = null;
            newAuthor = false;

        }

        //case when no affiliation is provided with the author
        if (qName.equalsIgnoreCase("author") & authorBuilder != null & currDocCurrAuthorAffiliation == null) {
            currAuthor = new Author(authorBuilder.toString());
            currDocSetAuthors.add(currAuthor);
            newAuthor = false;
        }

        if (qName.equalsIgnoreCase("title") & titleBuilder != null) {
            currDocTitle = titleBuilder.toString().replaceAll("\n\t", " ").trim();
            newTitle = false;
        }

        if (qName.matches("published")) {
            currDocYearPublished = Integer.parseInt(yearPublishedBuilder.toString().substring(0, 4));
            newYearPublished = false;
        }

        if (qName.matches("arxiv:affiliation")) {
            currDocCurrAuthorAffiliation = affiliationBuilder.toString().replaceAll("\n\t\r", " ").trim();
            newAffiliation = false;
        }

        if (qName.matches("arxiv:primary_category")) {
            if (mapPrimaryCategories.containsKey(primaryCategoryBuilder.toString().trim())) {
                currDocPrimaryCategory = mapPrimaryCategories.get(primaryCategoryBuilder.toString().trim());
            } else {
                currDocPrimaryCategory = "";
            }
            newPrimaryCategory = false;
        }

        if (qName.matches("entry")) {
            currDocument.setAuthors(currDocSetAuthors);
            currDocument.setTitle(currDocTitle);
            currDocument.setYear(currDocYearPublished);
            currDocument.setWhereFrom("arxiv");
            currDocument.setTopicArxiv(currDocPrimaryCategory);
//            currDocument.setCreationDateTime(new DateTime());
            if (currDocSetAuthors != null & currDocTitle != null & currDocYearPublished != null) {
                setArxivDocs.add(currDocument);
            }
            newEntry = false;
            nbArxivDocs++;
//            System.out.println("currDoc is: " + currDocument.toString());

        }
    }

    private void populateMapPrimaryCategories() {

        mapPrimaryCategories = new HashMap();
        mapPrimaryCategories.put("stat.AP", "Statistics - Applications");
        mapPrimaryCategories.put("stat.CO", "Statistics - Computation");
        mapPrimaryCategories.put("stat.ML", "Statistics - Machine Learning");
        mapPrimaryCategories.put("stat.ME", "Statistics - Methodology");
        mapPrimaryCategories.put("stat.TH", "Statistics - Theory");
        mapPrimaryCategories.put("q-bio.BM", "Quantitative Biology - Biomolecules");
        mapPrimaryCategories.put("q-bio.CB", "Quantitative Biology - Cell Behavior");
        mapPrimaryCategories.put("q-bio.GN", "Quantitative Biology - Genomics");
        mapPrimaryCategories.put("q-bio.MN", "Quantitative Biology - Molecular Networks");
        mapPrimaryCategories.put("q-bio.NC", "Quantitative Biology - Neurons and Cognition");
        mapPrimaryCategories.put("q-bio.OT", "Quantitative Biology - Other");
        mapPrimaryCategories.put("q-bio.PE", "Quantitative Biology - Populations and Evolution");
        mapPrimaryCategories.put("q-bio.QM", "Quantitative Biology - Quantitative Methods");
        mapPrimaryCategories.put("q-bio.SC", "Quantitative Biology - Subcellular Processes");
        mapPrimaryCategories.put("q-bio.TO", "Quantitative Biology - Tissues and Organs");
        mapPrimaryCategories.put("cs.AR", "Computer Science - Architecture");
        mapPrimaryCategories.put("cs.AI", "Computer Science - Artificial Intelligence");
        mapPrimaryCategories.put("cs.CL", "Computer Science - Computation and Language");
        mapPrimaryCategories.put("cs.CC", "Computer Science - Computational Complexity");
        mapPrimaryCategories.put("cs.CE", "Computer Science - Computational Engineering; Finance; and Science");
        mapPrimaryCategories.put("cs.CG", "Computer Science - Computational Geometry");
        mapPrimaryCategories.put("cs.GT", "Computer Science - Computer Science and Game Theory");
        mapPrimaryCategories.put("cs.CV", "Computer Science - Computer Vision and Pattern Recognition");
        mapPrimaryCategories.put("cs.CY", "Computer Science - Computers and Society");
        mapPrimaryCategories.put("cs.CR", "Computer Science - Cryptography and Security");
        mapPrimaryCategories.put("cs.DS", "Computer Science - Data Structures and Algorithms");
        mapPrimaryCategories.put("cs.DB", "Computer Science - Databases");
        mapPrimaryCategories.put("cs.DL", "Computer Science - Digital Libraries");
        mapPrimaryCategories.put("cs.DM", "Computer Science - Discrete Mathematics");
        mapPrimaryCategories.put("cs.DC", "Computer Science - Distributed; Parallel; and Cluster Computing");
        mapPrimaryCategories.put("cs.GL", "Computer Science - General Literature");
        mapPrimaryCategories.put("cs.GR", "Computer Science - Graphics");
        mapPrimaryCategories.put("cs.HC", "Computer Science - Human-Computer Interaction");
        mapPrimaryCategories.put("cs.IR", "Computer Science - Information Retrieval");
        mapPrimaryCategories.put("cs.IT", "Computer Science - Information Theory");
        mapPrimaryCategories.put("cs.LG", "Computer Science - Learning");
        mapPrimaryCategories.put("cs.LO", "Computer Science - Logic in Computer Science");
        mapPrimaryCategories.put("cs.MS", "Computer Science - Mathematical Software");
        mapPrimaryCategories.put("cs.MA", "Computer Science - Multiagent Systems");
        mapPrimaryCategories.put("cs.MM", "Computer Science - Multimedia");
        mapPrimaryCategories.put("cs.NI", "Computer Science - Networking and Internet Architecture");
        mapPrimaryCategories.put("cs.NE", "Computer Science - Neural and Evolutionary Computing");
        mapPrimaryCategories.put("cs.NA", "Computer Science - Numerical Analysis");
        mapPrimaryCategories.put("cs.OS", "Computer Science - Operating Systems");
        mapPrimaryCategories.put("cs.OH", "Computer Science - Other");
        mapPrimaryCategories.put("cs.PF", "Computer Science - Performance");
        mapPrimaryCategories.put("cs.PL", "Computer Science - Programming Languages");
        mapPrimaryCategories.put("cs.RO", "Computer Science - Robotics");
        mapPrimaryCategories.put("cs.SE", "Computer Science - Software Engineering");
        mapPrimaryCategories.put("cs.SD", "Computer Science - Sound");
        mapPrimaryCategories.put("cs.SC", "Computer Science - Symbolic Computation");
        mapPrimaryCategories.put("nlin.AO", "Nonlinear Sciences - Adaptation and Self-Organizing Systems");
        mapPrimaryCategories.put("nlin.CG", "Nonlinear Sciences - Cellular Automata and Lattice Gases");
        mapPrimaryCategories.put("nlin.CD", "Nonlinear Sciences - Chaotic Dynamics");
        mapPrimaryCategories.put("nlin.SI", "Nonlinear Sciences - Exactly Solvable and Integrable Systems");
        mapPrimaryCategories.put("nlin.PS", "Nonlinear Sciences - Pattern Formation and Solitons");
        mapPrimaryCategories.put("math.AG", "Mathematics - Algebraic Geometry");
        mapPrimaryCategories.put("math.AT", "Mathematics - Algebraic Topology");
        mapPrimaryCategories.put("math.AP", "Mathematics - Analysis of PDEs");
        mapPrimaryCategories.put("math.CT", "Mathematics - Category Theory");
        mapPrimaryCategories.put("math.CA", "Mathematics - Classical Analysis and ODEs");
        mapPrimaryCategories.put("math.CO", "Mathematics - Combinatorics");
        mapPrimaryCategories.put("math.AC", "Mathematics - Commutative Algebra");
        mapPrimaryCategories.put("math.CV", "Mathematics - Complex Variables");
        mapPrimaryCategories.put("math.DG", "Mathematics - Differential Geometry");
        mapPrimaryCategories.put("math.DS", "Mathematics - Dynamical Systems");
        mapPrimaryCategories.put("math.FA", "Mathematics - Functional Analysis");
        mapPrimaryCategories.put("math.GM", "Mathematics - General Mathematics");
        mapPrimaryCategories.put("math.GN", "Mathematics - General Topology");
        mapPrimaryCategories.put("math.GT", "Mathematics - Geometric Topology");
        mapPrimaryCategories.put("math.GR", "Mathematics - Group Theory");
        mapPrimaryCategories.put("math.HO", "Mathematics - History and Overview");
        mapPrimaryCategories.put("math.IT", "Mathematics - Information Theory");
        mapPrimaryCategories.put("math.KT", "Mathematics - K-Theory and Homology");
        mapPrimaryCategories.put("math.LO", "Mathematics - Logic");
        mapPrimaryCategories.put("math.MP", "Mathematics - Mathematical Physics");
        mapPrimaryCategories.put("math.MG", "Mathematics - Metric Geometry");
        mapPrimaryCategories.put("math.NT", "Mathematics - Number Theory");
        mapPrimaryCategories.put("math.NA", "Mathematics - Numerical Analysis");
        mapPrimaryCategories.put("math.OA", "Mathematics - Operator Algebras");
        mapPrimaryCategories.put("math.OC", "Mathematics - Optimization and Control");
        mapPrimaryCategories.put("math.PR", "Mathematics - Probability");
        mapPrimaryCategories.put("math.QA", "Mathematics - Quantum Algebra");
        mapPrimaryCategories.put("math.RT", "Mathematics - Representation Theory");
        mapPrimaryCategories.put("math.RA", "Mathematics - Rings and Algebras");
        mapPrimaryCategories.put("math.SP", "Mathematics - Spectral Theory");
        mapPrimaryCategories.put("math.ST", "Mathematics - Statistics");
        mapPrimaryCategories.put("math.SG", "Mathematics - Symplectic Geometry");
        mapPrimaryCategories.put("astro-ph", "Astrophysics");
        mapPrimaryCategories.put("cond-mat.dis-nn", "Physics - Disordered Systems and Neural Networks");
        mapPrimaryCategories.put("cond-mat.mes-hall", "Physics - Mesoscopic Systems and Quantum Hall Effect");
        mapPrimaryCategories.put("cond-mat.mtrl-sci", "Physics - Materials Science");
        mapPrimaryCategories.put("cond-mat.other", "Physics - Other");
        mapPrimaryCategories.put("cond-mat.soft", "Physics - Soft Condensed Matter");
        mapPrimaryCategories.put("cond-mat.stat-mech", "Physics - Statistical Mechanics");
        mapPrimaryCategories.put("cond-mat.str-el", "Physics - Strongly Correlated Electrons");
        mapPrimaryCategories.put("cond-mat.supr-con", "Physics - Superconductivity");
        mapPrimaryCategories.put("gr-qc", "General Relativity and Quantum Cosmology");
        mapPrimaryCategories.put("hep-ex", "High Energy Physics - Experiment");
        mapPrimaryCategories.put("hep-lat", "High Energy Physics - Lattice");
        mapPrimaryCategories.put("hep-ph", "High Energy Physics - Phenomenology");
        mapPrimaryCategories.put("hep-th", "High Energy Physics - Theory");
        mapPrimaryCategories.put("math-ph", "Mathematical Physics");
        mapPrimaryCategories.put("nucl-ex", "Nuclear Experiment");
        mapPrimaryCategories.put("nucl-th", "Nuclear Theory");
        mapPrimaryCategories.put("physics.acc-ph", "Physics - Accelerator Physics");
        mapPrimaryCategories.put("physics.ao-ph", "Physics - Atmospheric and Oceanic Physics");
        mapPrimaryCategories.put("physics.atom-ph", "Physics - Atomic Physics");
        mapPrimaryCategories.put("physics.atm-clus", "Physics - Atomic and Molecular Clusters");
        mapPrimaryCategories.put("physics.bio-ph", "Physics - Biological Physics");
        mapPrimaryCategories.put("physics.chem-ph", "Physics - Chemical Physics");
        mapPrimaryCategories.put("physics.class-ph", "Physics - Classical Physics");
        mapPrimaryCategories.put("physics.comp-ph", "Physics - Computational Physics");
        mapPrimaryCategories.put("physics.data-an", "Physics - Data Analysis; Statistics and Probability");
        mapPrimaryCategories.put("physics.flu-dyn", "Physics - Fluid Dynamics");
        mapPrimaryCategories.put("physics.gen-ph", "Physics - General Physics");
        mapPrimaryCategories.put("physics.geo-ph", "Physics - Geophysics");
        mapPrimaryCategories.put("physics.hist-ph", "Physics - History of Physics");
        mapPrimaryCategories.put("physics.ins-det", "Physics - Instrumentation and Detectors");
        mapPrimaryCategories.put("physics.med-ph", "Physics - Medical Physics");
        mapPrimaryCategories.put("physics.optics", "Physics - Optics");
        mapPrimaryCategories.put("physics.ed-ph", "Physics - Physics Education");
        mapPrimaryCategories.put("physics.soc-ph", "Physics - Physics and Society");
        mapPrimaryCategories.put("physics.plasm-ph", "Physics - Plasma Physics");
        mapPrimaryCategories.put("physics.pop-ph", "Physics - Popular Physics");
        mapPrimaryCategories.put("physics.space-ph", "Physics - Space Physics");
        mapPrimaryCategories.put("quant-ph", "Quantum Physics");
    }
}
