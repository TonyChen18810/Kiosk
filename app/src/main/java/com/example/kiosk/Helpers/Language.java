package com.example.kiosk.Helpers;

public class Language {

    private static int currentLanguage = 0;

    public static int getCurrentLanguage() {
        return currentLanguage;
    }

    public static void setCurrentLanguage(int language) {
        currentLanguage = language;
    }
}
