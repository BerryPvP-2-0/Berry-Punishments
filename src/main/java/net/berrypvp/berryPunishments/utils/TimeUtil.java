package net.berrypvp.berryPunishments.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtil {
    public static String formatTime(long milliseconds) {
        if (milliseconds == -1L)
            return "Permanent";
        long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
        milliseconds -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        milliseconds -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
        StringBuilder sb = new StringBuilder();
        if (days > 0L)
            sb.append(days).append("d ");
        if (hours > 0L)
            sb.append(hours).append("h ");
        if (minutes > 0L)
            sb.append(minutes).append("m ");
        if (seconds > 0L)
            sb.append(seconds).append("s");
        return sb.toString().trim();
    }

    public static long parseDuration(String duration) {
        if (duration.equalsIgnoreCase("permanent") || duration.equalsIgnoreCase("perm"))
            return -1L;
        long totalMillis = 0L;
        StringBuilder number = new StringBuilder();
        for (char c : duration.toCharArray()) {
            if (Character.isDigit(c)) {
                number.append(c);
            } else if (number.length() > 0) {
                long value = Long.parseLong(number.toString());
                switch (Character.toLowerCase(c)) {
                    case 'd':
                        totalMillis += TimeUnit.DAYS.toMillis(value);
                        break;
                    case 'h':
                        totalMillis += TimeUnit.HOURS.toMillis(value);
                        break;
                    case 'm':
                        totalMillis += TimeUnit.MINUTES.toMillis(value);
                        break;
                    case 's':
                        totalMillis += TimeUnit.SECONDS.toMillis(value);
                        break;
                }
                number.setLength(0);
            }
        }
        return totalMillis;
    }
}