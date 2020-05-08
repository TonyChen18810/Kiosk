package com.dbc.kiosk.Helpers;
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

    public static void setTime(String time, String date) {
        currentTime = time;
        StringBuilder dateBuilder = new StringBuilder();
        String[] dateArray = date.split("-");
        dateBuilder.append(dateArray[1]);
        dateBuilder.append("/");
        dateBuilder.append(dateArray[2]);
        dateBuilder.append("/");
        dateBuilder.append(dateArray[0]);
        // System.out.println("Web service date: " + dateBuilder.toString());
        currentDate = dateBuilder.toString();
        /*
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYYY");
        currentDate = sdf.format(c.getTime());
         */
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
