package com.elasticthree.projectparser;

/**
 * Created by mike on 8/28/16.
 */
public class DateUtils {
    public static String getNext8HourRange(int year, int day, int month, int hour) {
        StringBuilder dateRange = new StringBuilder();
        dateRange.append("\"").append(year).append("-").append(String.format("%02d", month)).append("-")
                .append(String.format("%02d", day)).append("T").append(String.format("%02d", hour))
                .append(":00:00Z").append(" .. ").append(year).append("-")
                .append(String.format("%02d", month)).append("-").append(String.format("%02d", day))
                .append("T").append(String.format("%02d", hour + 7)).append(":59:59Z").append("\"");
        return dateRange.toString();
    }
}
