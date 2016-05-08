package data;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javafx.util.Pair;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import model.Transaction;

/**
 * This class handles the opening, reading and saving of files. If a file is
 * requested that does not exist, it creates this file.
 *
 * @author Anaïs Ools
 */
public class XMLFileHandler {

    private final String m_filename;
    private final String m_filesLocation;
    private final ArrayList<Pair<String, Object>> m_content;
    private final ArrayList<Transaction> m_transactions;
    private boolean m_fatalError;

    // Constructor -------------------------------------------------------------
    public XMLFileHandler(String filename) {
        m_fatalError = false;
        m_content = new ArrayList();
        m_transactions = new ArrayList();
        m_filename = filename;
        m_filesLocation = System.getenv("APPDATA") + "\\GhostApps\\BoekhoudingXML\\";

        if (fileExists()) {
            readFile();
        } else {
            createFile();
        }
    }

    // Private functions -------------------------------------------------------
    /**
     * Check if the file exists.
     *
     * @return
     */
    private boolean fileExists() {
        File f = new File(m_filesLocation + m_filename);
        return f.exists() && !f.isDirectory();
    }

    /**
     * Read the data from the file into the provided lists.
     */
    private void readFile() {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            CustomXMLHandler handler = new CustomXMLHandler();
            saxParser.parse(m_filesLocation + m_filename, handler);
            System.out.println(handler.getElements().toString());
            parseXMLToObjects(handler.getElements());
        } catch (ParserConfigurationException | org.xml.sax.SAXException | IOException ex) {
            System.out.println(ex.getMessage());
            m_fatalError = true;
        }
    }

    /**
     * Create a new file.
     */
    private void createFile() {
        // create folder if not exists
        File f = new File(m_filesLocation);
        if (f.exists() && f.isDirectory()) {
        } else {
            f.mkdir();
        }

        // create file if not exists
        f = new File(m_filesLocation + m_filename);
        if (f.exists() && !f.isDirectory()) {
        } else {
            ArrayList<String> lines = new ArrayList();
            lines.add("<?xml version=\"1.0\"?>");
            lines.add("<data>\n</data>");
            writeToFile(lines);
        }
    }

    /**
     * Write a list of strings to the file.
     *
     * @param lines
     */
    private void writeToFile(ArrayList<String> lines) {
        try {
            Path file = Paths.get(m_filesLocation + m_filename);
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            m_fatalError = true;
        }
    }

    /**
     * Parse a tree-structure of XElements to transactions or strings.
     *
     * @param root
     */
    private void parseXMLToObjects(XElement root) {
        for (XElement element : root.getChildren()) {
            if (element.getName().equals("transaction")) {
                parseXMLToTransaction(element);
            } else {
                parseXMLToString(element);
            }
        }
    }

    /**
     * Parse an element of an XML-structure to a transaction and add it to the
     * list of transactions.
     *
     * @param transaction
     */
    private void parseXMLToTransaction(XElement transaction) {
        String[] requiredElements = new String[]{
            "ID", "description", "price", "category",
            "transactor", "transactorCategory",
            "dateAdded", "datePaid",
            "paymentMethod", "paymentMethodCategory"
        };
        for (String s : requiredElements) {
            if (!transaction.hasChild(s)) {
                return;
            }
            XElement child = transaction.getChild(s);
            if (child.getValue() == null || child.getValue().equals("")) {
                return;
            }
        }
        // Element represents a valid transaction now
        Transaction t = new Transaction(Long.parseLong(transaction.getChild("ID").getValue()));
        for (XElement child : transaction.getChildren()) {
            String value = child.getValue();
            switch (child.getName()) {
                case "description":
                    t.setDescription(value);
                    break;
                case "price":
                    t.setPrice(Double.parseDouble(value));
                    break;
                case "category":
                    t.setCategory(value);
                    break;
                case "transactor":
                    t.setTransactor(value, transaction.getChild("transactorCategory").getValue());
                    break;
                case "dateAdded":
                    t.setDateAdded(stringToDate(value));
                    break;
                case "datePaid":
                    t.setDatePaid(stringToDate(value));
                    break;
                case "paymentMethod":
                    t.setPaymentMethod(value, transaction.getChild("paymentMethodCategory").getValue());
                    break;
            }
        }
        m_transactions.add(t);
    }

    /**
     * Parse a string to a date. If this fails, the function returns a new date
     * object.
     *
     * @param s
     * @return
     */
    private Date stringToDate(String s) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return df.parse(s);
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
            m_fatalError = true;
            return new Date();
        }
    }

    /**
     * Parse an element of an XML-structure to a string and add it to the list
     * of strings.
     *
     * @param e
     */
    private void parseXMLToString(XElement e) {
        // TODO
    }

    // Public functions --------------------------------------------------------
    /**
     * Get the content of the file as a list of key-value pairs. Transactions do
     * not appear in this list.
     *
     * @return
     */
    public ArrayList<Pair<String, Object>> getContent() {
        return m_content;
    }

    /**
     * Get a list of all the transactions in the file. If the file has no
     * transactions, this list will be empty.
     *
     * @return
     */
    public ArrayList<Transaction> getTransactions() {
        return m_transactions;
    }

    /**
     * Check if the loading of the files succeeded or not.
     *
     * @return
     */
    public boolean success() {
        return !m_fatalError;
    }

    // Private classes ---------------------------------------------------------
    private class CustomXMLHandler extends DefaultHandler {

        private XElement m_root;
        private XElement m_current;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (m_root == null) {
                m_root = new XElement(qName);
                m_current = m_root;
            } else {
                XElement e = new XElement(qName);
                m_current.addChild(e);
                m_current = e;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (!m_current.equals(m_root)) {
                m_current = m_current.getParent();
            }
        }

        @Override
        public void characters(char ch[], int start, int length) throws SAXException {
            String value = new String(ch, start, length);
            value = value.trim();
            if (!"".equals(value) && !"\n".equals(value)) {
                System.out.println("\t\t\t" + value);
                m_current.setValue(value);
            }
        }

        public XElement getElements() {
            return m_root;
        }
    }

    private class XElement {

        private final String m_name;
        private String m_value;
        private XElement m_parent;
        private final ArrayList<XElement> m_children;

        public XElement(String name) {
            m_name = name;
            m_children = new ArrayList();
        }

        public void addChild(XElement child) {
            m_children.add(child);
            child.setParent(this);
        }

        public void setParent(XElement parent) {
            m_parent = parent;
        }

        public XElement getParent() {
            return m_parent;
        }

        public void setValue(String value) {
            m_value = value;
        }

        public String getValue() {
            return m_value;
        }

        public String getName() {
            return m_name;
        }

        private ArrayList<XElement> getChildren() {
            return m_children;
        }

        public boolean hasChild(String name) {
            for (XElement child : m_children) {
                if (child.getName().equals(name)) {
                    return true;
                }
            }
            return false;
        }

        public XElement getChild(String name) {
            for (XElement child : m_children) {
                if (child.getName().equals(name)) {
                    return child;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return this.toString(0);
        }

        private String toString(int tabs) {
            String tab = "    ";
            String s = "";

            // opening tag
            for (int i = 0; i < tabs; i++) {
                s += tab;
            }
            s += "<" + m_name + ">";

            // value
            if (m_value != null) {
                if (m_children.size() > 0) {
                    s += "\n";
                    for (int i = 0; i < tabs + 1; i++) {
                        s += tab;
                    }
                }
                s += m_value;
            }
            if (m_children.size() > 0) {
                s += "\n";
            }

            // children
            for (XElement child : m_children) {
                s += child.toString(tabs + 1);
            }
            if (m_children.size() > 0) {
                for (int i = 0; i < tabs; i++) {
                    s += tab;
                }
            }
            s += "</" + m_name + ">\n";
            return s;
        }

    }

}
