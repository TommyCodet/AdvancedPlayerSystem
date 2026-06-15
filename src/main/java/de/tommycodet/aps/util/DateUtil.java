package de.tommycodet.aps.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for date and time operations.
 */
public class DateUtil {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Converts a timestamp to a readable date string.
     *
     * @param timestamp The timestamp in milliseconds
     * @return Formatted date string
     */
    public static String formatDate(long timestamp) {
        return DATE_FORMAT.format(new Date(timestamp));
    }

    /**
     * Converts seconds to a readable playtime string.
     *
     * @param seconds The number of seconds
     * @return Formatted playtime (e.g., "1d 2h 30m")
     */
    public static String formatPlaytime(long seconds) {
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (days > 0) {
            return String.format("%dd %dh %dm %ds", days, hours, minutes, secs);
        } else if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, secs);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, secs);
        } else {
            return String.format("%ds", secs);
        }
    }

    /**
     * Gets the current timestamp in milliseconds.
     *
     * @return Current timestamp
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
}
