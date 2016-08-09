package model;

import java.util.Comparator;

/**
 * Comparator for the CategoryString class.
 *
 * @author Anaïs Ools
 */
public class CategoryStringComparator implements Comparator<CategoryString> {

    @Override
    public int compare(CategoryString t, CategoryString t1) {
        return t.toString().compareTo(t1.toString());
    }

}
