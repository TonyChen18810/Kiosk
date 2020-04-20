package com.dbc.kiosk.Helpers;

import java.text.SimpleDateFormat;
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

    public static void setTime(String time) {
        SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm");
        String currentDateandTime = parseFormat.format(new Date());
        // System.out.println("currentDateandTime: " + time);
        currentTime = time; // was currentDateandTime
    }

    public static String getCurrentTime() {
        return currentTime;
    }
}
