package com.dbc.kiosk.Helpers;

/**
 * Language.java
 *
 * Used to keep track of current language value (0 = English, 1 = Spanish, 2 = French)
 *
 */
public class Language {

    private static int currentLanguage = 1;

    public static int getCurrentLanguage() {
        return currentLanguage;
    }

    public static void setCurrentLanguage(int language) {
        System.out.println("CURRENT LANGUAGE: " + language);
        currentLanguage = language;
    }
}
