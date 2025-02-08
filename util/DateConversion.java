package com.project.krishna.kaam.util;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConversion {


    public static String fromMiliToDate(DateTime td) {
        String year = String.valueOf(td.getYear());
        String month = td.monthOfYear().getAsText();
        String day = String.valueOf(td.getDayOfMonth());
        String wd = td.dayOfWeek().getAsText();
        String fulldate = month + " " + day + " " + year + "," + wd;
        return fulldate;
    }

    public static String toAMPM(DateTime td) {
        LocalTime time = new LocalTime(td.getHourOfDay() + ":" + td.getMinuteOfHour());
        DateTimeFormatter fmt = DateTimeFormat.forPattern("h:mm a");
        return fmt.print(time);
    }

    public static long getDateTime(DateTime dateTime, LocalTime localTime) {
        long l = dateTime.getMillis();


        String toParse = dateTime.getYear() + "-" + dateTime.getMonthOfYear() + "-" + dateTime.getDayOfMonth() +
                " " + localTime.getHourOfDay() + ":" + localTime.getMinuteOfHour() + ":00.0";


        //   String toParse = "2016-01-09 21:04:56.0";
        SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        Date newDate = null;
        try {
            newDate = sdfIn.parse(toParse);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long dateTimeInMili = newDate.getTime();
        //  Log.i("TAG","in mili"+s);
        //Log.i("TAG","back to date"+new DateTime(s).toString());

        return dateTimeInMili;

    }

    public static String getTimein24(long dateTime) {
        DateTime date = new DateTime(dateTime);
        int hour = date.getHourOfDay();
        int minute = date.getMinuteOfHour();
        String time24 = hour + ":" + minute;
        return time24;
    }

    public static boolean isToday(DateTime time) {
        return LocalDate.now().compareTo(new LocalDate(time)) == 0;
    }


    public static boolean isTomorrow(DateTime time) {
        return LocalDate.now().plusDays(1).compareTo(new LocalDate(time)) == 0;
    }

    public static boolean isYesterday(DateTime time) {
        return LocalDate.now().minusDays(1).compareTo(new LocalDate(time)) == 0;
    }
}
