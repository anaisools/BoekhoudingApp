package view.swingextensions;

import java.util.Comparator;
import java.util.Date;

/**
 * Compare two objects as numbers if they are numeric values. Otherwise, compare
 * their string-representation.
 *
 * @author Anaïs Ools
 */
public class TableCellComparator implements Comparator {

    @Override
    public int compare(Object a, Object b) {
        if (a.getClass().equals(Double.class)) {
            return ((Double) a).compareTo((Double) b);
        } else if (a.getClass().equals(Date.class)) {
            return ((Date) a).compareTo((Date) b);
        } else {
            return a.toString().toLowerCase().compareTo(b.toString().toLowerCase());
        }
    }

}
