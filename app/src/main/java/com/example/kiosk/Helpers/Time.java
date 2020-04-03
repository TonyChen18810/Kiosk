package com.example.kiosk.Helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {

    private static String currentTime;

    public static void setTime() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm");
        String currentDateandTime = parseFormat.format(new Date());
        System.out.println(currentDateandTime);
        currentTime = currentDateandTime;
    }

    public static void setTestingTime(String time) {
        currentTime = time;
    }

    public static String getCurrentTime() {
        return currentTime;
    }
}
