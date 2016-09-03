package com.elasticthree.projectparser;


import java.util.Iterator;

class ParserDateUtils implements  Iterable<String> {

    int year;
    int month = 1;
    int day = 1;
    int hour = 0;

    public ParserDateUtils(int year) {
        this.year = year;
    }

    String getNext8HourRange() {
        StringBuilder dateRange = new StringBuilder();
        dateRange.append("\"").append(year).append("-").append(String.format("%02d", month)).append("-")
                .append(String.format("%02d", day)).append("T").append(String.format("%02d", hour))
                .append(":00:00Z").append(" .. ").append(year).append("-")
                .append(String.format("%02d", month)).append("-").append(String.format("%02d", day))
                .append("T").append(String.format("%02d", hour + 7)).append(":59:59Z").append("\"");
        return dateRange.toString();
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            @Override
            public boolean hasNext() {
                return (month < 13);
            }

            @Override
            public String next() {
                String ret = getNext8HourRange();
                month = getNextMonthIfApplicable();
                day = getNextDayIfApplicable();
                hour = getNext8hour();

                return ret;
            }

            private int getNext8hour() {
                return hour == 16 ? 0 : (hour + 8);
            }

            private int getNextDayIfApplicable() {
                if (hour == 16) {
                    if (day == 31)
                        return 0;
                    return ++day;
                }
                return day;
            }

            private int getNextMonthIfApplicable() {
                return ((day == 31) && (hour == 16)) ? ++month : month;
            }
        };
    }
}
