package com.example.kiosk;

import java.util.Calendar;

class Time {

    private static String currentTime;

    static void setTime() {
        currentTime = Calendar.getInstance().getTime().toString();
    }

    static String getCurrentTime() {
        return currentTime;
    }

}
