package boekhoudingxml;

import data.Data;
import java.io.*;
import javax.swing.JOptionPane;
import view.MainWindow;

/**
 * The main class which initializes the data and opens the main frame.
 *
 * @author Anaïs Ools
 */
public class BoekhoudingXML {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                /* PRINT OUT AND ERR TO FILES FOR BUG FIXING
                try {
                    File out = new File(System.getenv("APPDATA") + "\\GhostApps\\BoekhoudingApp\\out.txt");
                    File err = new File(System.getenv("APPDATA") + "\\GhostApps\\BoekhoudingApp\\err.txt");
                    FileOutputStream fOut = new FileOutputStream(out);
                    FileOutputStream fErr = new FileOutputStream(err);
                    System.setOut(new PrintStream(fOut));
                    System.setErr(new PrintStream(fErr));
                } catch (FileNotFoundException ex) {
                    System.out.println("Cannot open error streams");
                }
                */

                BoekhoudingXML b = new BoekhoudingXML();
            }
        });
    }

    /**
     * Create a new instance of the application. Initializes the data and opens
     * a frame.
     */
    public BoekhoudingXML() {
        if (loadData()) {
            openFrame();
        } else {
            JOptionPane.showMessageDialog(null, "Something went wrong when loading the data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean loadData() {
        return Data.GetInstance().loadingDataSucceeded();
    }

    private void openFrame() {
        MainWindow mw = new MainWindow();
        mw.showFrame();
    }
}
