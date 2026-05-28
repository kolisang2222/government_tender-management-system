package util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Utility class for converting between different date and time representations.
 * Provides methods to convert Java 8 {@link LocalDateTime} objects to legacy
 * {@link Date} objects.
 */
public class DateConverter {

    /**
     * Converts a {@link LocalDateTime} object to a {@link Date} object using
     * the system default time zone.
     *
     * @param localDateTime the {@code LocalDateTime} to convert, may be
     * {@code null}
     * @return a {@code Date} object representing the same point on the timeline
     * as the given {@code LocalDateTime}, or {@code null} if the input was
     * {@code null}
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
