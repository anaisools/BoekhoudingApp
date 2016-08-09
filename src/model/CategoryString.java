package model;

/**
 * This class represents a string with a certain category-string. Both strings
 * can be set and retrieved separately. When the object is converted to a
 * string, they are appended in the following way: Category > String.
 *
 * @author Anaïs Ools
 */
public class CategoryString {

    private String m_category;
    private String m_value;

    public CategoryString() {
    }

    public CategoryString(String categoryValueCombination) {
        set(categoryValueCombination);
    }

    public CategoryString(String category, String value) {
        set(category, value);
    }

    public void setCategory(String category) {
        m_category = category;
    }

    public void setValue(String value) {
        m_value = value;
    }

    public void set(String category, String value) {
        setCategory(category);
        setValue(value);
    }

    /**
     * If the string does not match the pattern "Category > Value", then nothing
     * will be set.
     *
     * @param categoryValueCombination
     */
    public void set(String categoryValueCombination) {
        if (categoryValueCombination.contains(" > ")) {
            String[] split = categoryValueCombination.split(" > ");
            if (split.length >= 2) {
                setCategory(split[0]);
                setValue(split[1]);
            }
        }
    }

    public String getCategory() {
        return m_category;
    }

    public String getValue() {
        return m_value;
    }

    @Override
    public String toString() {
        return m_category + " > " + m_value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !o.getClass().equals(CategoryString.class)) {
            return false;
        }
        CategoryString cs = (CategoryString) o;
        return this.toString().equals(cs.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
