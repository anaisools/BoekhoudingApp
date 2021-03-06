package data;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.text.*;
import java.util.*;
import javafx.util.Pair;
import javax.xml.parsers.*;
import model.CategoryString;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import model.Transaction;
import model.Transaction.TRANSACTIONFIELD;

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
    private final String m_metaData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
    private final SimpleDateFormat m_dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    // Constructor -------------------------------------------------------------
    public XMLFileHandler(String filename) {
        m_fatalError = false;
        m_content = new ArrayList();
        m_transactions = new ArrayList();
        m_filename = filename;
        m_filesLocation = System.getenv("APPDATA") + "\\GhostApps\\BoekhoudingApp\\";

        if (!fileExists()) {
            createFile();
        }
    }

    public XMLFileHandler(String fileLocation, String filename) {
        m_fatalError = false;
        m_content = new ArrayList();
        m_transactions = new ArrayList();
        m_filename = filename;
        if (!fileLocation.endsWith("\\")) {
            fileLocation += "\\";
        }
        m_filesLocation = fileLocation;

        if (!fileExists()) {
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
            parseXMLToObjects(handler.getElements());
        } catch (ParserConfigurationException | org.xml.sax.SAXException | IOException ex) {
            System.err.println(ex.getMessage());
            m_fatalError = true;
        }
    }

    /**
     * Create a new file.
     */
    private void createFile() {
        // create folder if not exists
        createFolder(m_filesLocation);

        // create file if not exists
        File f = new File(m_filesLocation + m_filename);
        if (f.exists() && !f.isDirectory()) {
        } else {
            ArrayList<String> lines = new ArrayList();
            lines.add(m_metaData);
            lines.add("<data>\n</data>");
            writeToFile(lines);
        }
    }

    /**
     * Create a folder if it does not exist. If it already exists, nothing will
     * happen. Non-existing parent folders will be recursively created.
     *
     * @param fileLocation
     */
    private void createFolder(String fileLocation) {
        while (fileLocation.charAt(fileLocation.length() - 1) == '\\') {
            fileLocation = fileLocation.substring(0, fileLocation.length() - 1);
        }
        File f = new File(fileLocation);
        if (!f.exists()) {
            String parentFolder = fileLocation.substring(0, fileLocation.lastIndexOf("\\"));
            createFolder(parentFolder);
            f.mkdir();
            System.out.println("Creating folder " + fileLocation);
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
            System.err.println(ex.getMessage());
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
        if (!transaction.hasChild("ID")) {
            System.out.println("Transaction not added because no ID found.");
            return;
        }
        long id = Long.parseLong(transaction.getChild("ID").getValue());
        Transaction t = new Transaction(id);

        ArrayList<TRANSACTIONFIELD> requiredFields = t.requiredFields();
        for (XElement child : transaction.getChildren()) {
            String name = child.getName();
            Object value = child.getValue();
            if (!name.equals("ID")) {
                TRANSACTIONFIELD field = t.stringToTransactionField(name);
                if (field == null) {
                    System.out.println("Transaction " + id + " has invalid field \"" + name + "\" and was not parsed.");
                    return;
                }
                requiredFields.remove(field);
                Class preferredClass = t.getFieldClass(field);
                if (preferredClass.equals(Double.class)) {
                    value = Double.parseDouble((String) value);
                } else if (preferredClass.equals(Boolean.class)) {
                    value = Boolean.parseBoolean((String) value);
                } else if (preferredClass.equals(Date.class)) {
                    value = stringToDate((String) value);
                } else if (preferredClass.equals(CategoryString.class)) {
                    value = new CategoryString((String) value);
                }
                t.set(field, value);
            }
        }
        if (requiredFields.size() > 0) { // not all required fields were found
            System.out.print("Transaction " + id + " does not contain required field(s): ");
            for (TRANSACTIONFIELD field : requiredFields) {
                System.out.print("\"" + field.toString().toLowerCase() + "\" ");
            }
            System.out.println("and was not parsed.");
            return;
        }
        m_transactions.add(t);
    }

    /**
     * Parse an element of an XML-structure to a string and add it to the list
     * of strings.
     *
     * @param e
     */
    private void parseXMLToString(XElement e) {
        if (e.getName() != null && e.getValue() != null) {
            m_content.add(new Pair(e.getName(), e.getValue()));
        }
    }

    /**
     * Parse a list of transactions to an XML structure.
     *
     * @param list
     * @return
     */
    private XElement parseTransactionsToXML(ArrayList<Transaction> list) {
        XElement root = new XElement("data");
        for (Transaction t : list) {
            root.addChild(parseTransactionToXML(t));
        }
        return root;
    }

    /**
     * Parse a single transaction to an XML structure.
     *
     * @param t
     * @return
     */
    private XElement parseTransactionToXML(Transaction t) {
        XElement e = new XElement("transaction");
        e.addChild(new XElement("ID", objectToString(t.getID())));
        for (TRANSACTIONFIELD f : t.presentFields()) {
            e.addChild(new XElement(t.transactionFieldToString(f), objectToString(t.get(f))));
        }
        return e;
    }

    /**
     * Converts any object to a string.
     *
     * @param o
     * @return
     */
    private String objectToString(Object o) {
        if (o == null) {
            return "";
        }
        if (o.getClass().equals(CategoryString.class)) {
            o = ((CategoryString) o).toString();
        }
        if (o.getClass().equals(String.class)) {
            return ((String) o).replace("&", "&amp;");
        } else if (o.getClass().equals(Date.class)) {
            return dateToString((Date) o);
        } else {
            return String.valueOf(o);
        }
    }

    /**
     * Parse a string to a date. If this fails, the function returns a new date
     * object.
     *
     * @param s
     * @return
     */
    private Date stringToDate(String s) {
        try {
            return m_dateFormat.parse(s);
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
            m_fatalError = true;
            return new Date();
        }
    }

    /**
     * Parse a date to a string.
     *
     * @param d
     * @return
     */
    private String dateToString(Date d) {
        return m_dateFormat.format(d);
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
     * Get a list of loaded transactions. If the file has no transactions, this
     * list will be empty.
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

    /**
     * Get a list of transactions from the file.
     */
    public void loadTransactions() {
        readFile();
    }

    /**
     * Get a list of settings from the file.
     */
    public void loadSettings() {
        readFile();
    }

    /**
     * Save a list of transactions to the file.
     *
     * @param list
     */
    public void saveTransactions(ArrayList<Transaction> list) {
        ArrayList<String> result = new ArrayList();
        result.add(m_metaData);
        String xml = parseTransactionsToXML(list).toString();
        for (String s : xml.split("\n")) {
            result.add(s);
        }
        writeToFile(result);
    }

    /**
     * Save a list of settings to the file.
     *
     * @param list
     */
    public void saveSettings(ArrayList<Pair<String, Object>> list) {
        ArrayList<String> result = new ArrayList();
        result.add(m_metaData);
        XElement root = new XElement("data");
        for (Pair<String, Object> p : list) {
            root.addChild(new XElement(p.getKey(), String.valueOf(p.getValue())));
        }
        String xml = root.toString();
        for (String s : xml.split("\n")) {
            result.add(s);
        }
        writeToFile(result);
    }

    // Private classes ---------------------------------------------------------
    private class CustomXMLHandler extends DefaultHandler {

        private XElement m_root;
        private XElement m_current;
        private String m_value;
        private boolean m_elementHasEnded;

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
            m_elementHasEnded = false;
            m_value = "";
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            m_elementHasEnded = true;
            if (!"".equals(m_value) && !"\n".equals(m_value)) {
                m_current.setValue(m_value);
            }
            if (!m_current.equals(m_root)) {
                m_current = m_current.getParent();
            }
        }

        @Override
        public void characters(char ch[], int start, int length) throws SAXException {
            String value = new String(ch, start, length);
            //value = value.trim();
            if (!m_elementHasEnded && !"".equals(value) && !"\n".equals(value)) {
                m_value += value;
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

        public XElement(String name, String value) {
            m_name = name;
            m_value = value;
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
