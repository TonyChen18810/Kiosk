package com.dbc.kiosk.Helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    public static void setTime(String time) {
        currentTime = time;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYYY");
        currentDate = sdf.format(c.getTime());
        System.out.println("today's date: " + currentDate);
    }

    public static String getCurrentTime() {
        return currentTime;
    }

    public static String getCurrentDate() {
        return currentDate;
    }
}
