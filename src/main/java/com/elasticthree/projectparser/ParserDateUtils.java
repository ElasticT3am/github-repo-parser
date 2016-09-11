package com.elasticthree.projectparser;


import java.util.Iterator;

class ParserDateUtils implements  Iterable<String> {

    private int year;
    private int month;
    private int untilMonth;
    private int day;
    private int hour;

    ParserDateUtils(int year, int month, int untilMonth) {
        this.year = year;
        this.month = month;
        this.untilMonth = untilMonth;
        this.day = 1;
        this.hour = 0;
    }



    private String getNext8HourRange() {
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
                return (month <= untilMonth);
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
