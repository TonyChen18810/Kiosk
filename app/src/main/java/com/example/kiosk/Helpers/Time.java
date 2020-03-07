package com.example.kiosk.Helpers;

import java.util.Calendar;

public class Time {

    private static String currentTime;

    public static void setTime() {
        currentTime = Calendar.getInstance().getTime().toString();
    }

    public static String getCurrentTime() {
        return currentTime;
    }

}
