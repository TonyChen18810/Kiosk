package com.dbc.kiosk.Helpers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Time.java
 *
 * Called from LoggedIn.java to save the time the user logged in
 * and eventually compare to order appointment times and decide
 * whether the user is early/late/on-time
 */
public class Time {

    private static String currentTime;
    private static String currentDate;

    public static void setTime(String time, String date) throws ParseException {
        currentTime = time;
        String tz = TimeZone.getDefault().getDisplayName();
        if (tz.equals("Mountain Standard Time")) {
            int hour = Integer.parseInt(time.substring(0,2));
            hour += 1;
            if (hour < 10) {
                StringBuilder temp = new StringBuilder();
                temp.append("0");
                temp.append(hour);
                temp.append(time.substring(2));
                currentTime = temp.toString();
            } else {
                StringBuilder temp = new StringBuilder();
                temp.append(hour);
                temp.append(time.substring(2));
                currentTime = temp.toString();
            }
        }
        StringBuilder dateBuilder = new StringBuilder();
        String[] dateArray = date.split("-");
        dateBuilder.append(dateArray[1]);
        dateBuilder.append("/");
        dateBuilder.append(dateArray[2]);
        dateBuilder.append("/");
        dateBuilder.append(dateArray[0]);
        currentDate = dateBuilder.toString();
        System.out.println("today's date: " + currentDate);
    }

    public static String getCurrentTime() {
        return currentTime;
    }

    public static String getCurrentDate() {
        return currentDate;
    }

    public static void resetTimeAndDate() {
        currentTime = null;
        currentDate = null;
    }
}
