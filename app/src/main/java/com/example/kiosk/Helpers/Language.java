package com.example.kiosk.Helpers;

/**
 * Language.java
 *
 * Used to keep track of current language value (0 = English, 1 = Spanish, 2 = French)
 *
 * When a web service is called that requires a language preference value passed in,
 * we use Integer.toString(getCurrentLanguage + 1) because the web service stores
 * the languages as (1 = English, 2 = Spanish, 3 = French)
 */
public class Language {

    private static int currentLanguage = 0;

    public static int getCurrentLanguage() {
        return currentLanguage;
    }

    public static void setCurrentLanguage(int language) {
        currentLanguage = language;
    }
}
