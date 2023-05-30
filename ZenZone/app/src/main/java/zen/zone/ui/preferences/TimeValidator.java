package zen.zone.ui.preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeValidator {

    public static boolean isValidTime(String time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
        dateFormat.setLenient(false);

        try {
            Date parsedDate = dateFormat.parse(time);
            return parsedDate != null;
        } catch (ParseException e) {
            return false;
        }
    }
}
