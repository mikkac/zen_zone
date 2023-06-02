package zen.zone.ui.preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * The TimeValidator class provides utility methods for validating time strings.
 */
public class TimeValidator {

    /**
     * Checks if a given time string is a valid time in HH:mm format.
     *
     * @param time The time string to validate.
     * @return true if the time string is valid, false otherwise.
     */
    public static boolean isValidTime(String time) {
        // Use SimpleDateFormat to parse the time string.
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
        // Set lenient to false to ensure strict parsing and validation of the time string.
        dateFormat.setLenient(false);

        try {
            // Try to parse the time string.
            Date parsedDate = dateFormat.parse(time);
            // If the parsing is successful and the parsed date is not null,
            // then the time string is valid.
            return parsedDate != null;
        } catch (ParseException e) {
            // If a ParseException is thrown, then the time string is not valid.
            return false;
        }
    }
}
