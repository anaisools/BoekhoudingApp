package view.swingextensions;

/**
 * This class extends a JTextField such that it only accepts dates in the format
 * of dd/MM/yyyy.
 *
 * @author Anaïs Ools
 */
public class DateTextField extends ValidationTextField {

    public DateTextField(boolean canBeEmpty) {
        super(canBeEmpty);
        this.setPlaceholder("dd/mm/yyyy");
    }

    public DateTextField(boolean canBeEmpty, String initialValue) {
        super(canBeEmpty);
        this.setPlaceholder("dd/mm/yyyy");
        this.setText(initialValue);
    }

    @Override
    protected void validateText() {
        String text = "";
        // filter characters
        for (char c : this.getText().toCharArray()) {
            if ((c >= '0' && c <= '9') || c == '/') {
                text += c;
            } else if (c == '-' || c == '.') {
                text += '/';
            }
        }

        // correct pattern
        String[] parts = text.split("/");
        if (parts.length != 3) {
            setValid(false);
            return;
        } else {
            String dayString = parts[0];
            String monthString = parts[1];
            String yearString = parts[2];
            if (dayString.length() > 2) {
                setValid(false);
                return;
            }
            if (dayString.length() < 2) {
                dayString = "0" + dayString;
            }
            if (monthString.length() > 2) {
                setValid(false);
                return;
            }
            if (monthString.length() < 2) {
                monthString = "0" + monthString;
            }
            if (yearString.length() > 4) {
                setValid(false);
                return;
            }
            if (yearString.length() == 2) {
                yearString = "20" + yearString;
            }
            if (yearString.length() != 4) {
                setValid(false);
                return;
            }
            text = dayString + "/" + monthString + "/" + yearString;
            this.setText(text);
        }
        // check pattern
        if (text.length() != 10) {
            setValid(false);
            return;
        }
        // check pattern
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (i == 2 || i == 5) {
                if (c != '/') {
                    System.out.println("No / at " + i);
                    setValid(false);
                    return;
                }
            } else {
                if (!(c >= '0' && c <= '9')) {
                    System.out.println("No number at " + i);
                    setValid(false);
                    return;
                }
            }
        }
        // validate date
        int day = Integer.parseInt(text.substring(0, 2));
        int month = Integer.parseInt(text.substring(3, 5));
        int year = Integer.parseInt(text.substring(6, 10));
        if (day < 1 || day > 31 || month < 1 || month > 12) {
            setValid(false);
            return;
        }
        if (month == 2) {
            if (year % 4 == 0 && day > 29) {
                setValid(false);
                return;
            } else if (year % 4 != 0 && day > 28) {
                setValid(false);
                return;
            }
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            if (day > 30) {
                setValid(false);
                return;
            }
        }
        setValid(true);
    }

}
