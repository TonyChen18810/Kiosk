package com.example.kiosk;

public class Language {

    private static int currentLanguage = 0;

    static int getCurrentLanguage() {
        return currentLanguage;
    }

    static void setCurrentLanguage(int language) {
        currentLanguage = language;
    }
}
