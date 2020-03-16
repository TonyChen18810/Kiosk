package com.example.kiosk;

import java.util.ArrayList;

public class Account {

    private String email;
    private String phoneNumber;
    private String truckName;
    private String truckNumber;
    private String trailerLicense;
    private String trailerState;
    private String driverLicense;
    private String driverState;
    private String driverName;
    private String dispatcherPhoneNumber;
    private String languagePreference;
    private String communicationPreference;

    private static Account CURRENT_ACCOUNT;

    private static ArrayList<Account> accounts = new ArrayList<>();

    public Account(String email, String driverName, String phoneNumber, String truckName, String truckNumber, String driverLicense, String driverState,
                   String trailerLicense, String trailerState, String dispatcherPhoneNumber, String languagePreference, String communicationPreference) {
        this.email = email;
        this.driverName = driverName;
        this.phoneNumber = phoneNumber;
        this.truckName = truckName;
        this.truckNumber = truckNumber;
        this.driverLicense = driverLicense;
        this.driverState = driverState;
        this.trailerLicense = trailerLicense;
        this.trailerState = trailerState;
        this.dispatcherPhoneNumber = dispatcherPhoneNumber;
        this.languagePreference = languagePreference;
        this.communicationPreference = communicationPreference;
    }

    public String getEmail() {
        return email;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getTruckName() {
        return truckName;
    }

    public String getTruckNumber() {
        return truckNumber;
    }

    public String getDriverLicense() {
        return driverLicense;
    }

    public String getDriverState() { return driverState; }

    public String getTrailerLicense() { return trailerLicense; }

    public String getTrailerState() { return trailerState; }

    public String getDispatcherPhoneNumber() {
        return dispatcherPhoneNumber;
    }

    public String getLanguagePreference() {
        return languagePreference;
    }

    public String getCommunicationPreference() {
        return communicationPreference;
    }

    public static ArrayList<Account> getAccounts() {
        return accounts;
    }

    public static void addAccount(Account account) {
        accounts.add(account);
    }

    public static void clearAccounts() {
        accounts.clear();
    }

    public static Account getCurrentAccount() {
        return CURRENT_ACCOUNT;
    }

    public static void setCurrentAccount(Account account) {

        CURRENT_ACCOUNT = account;
        /*
        System.out.println(CURRENT_ACCOUNT.email);
        System.out.println(CURRENT_ACCOUNT.driverName);
        System.out.println(CURRENT_ACCOUNT.phoneNumber);
        System.out.println(CURRENT_ACCOUNT.truckName);
        System.out.println(CURRENT_ACCOUNT.truckNumber);
        System.out.println(CURRENT_ACCOUNT.driverLicense);
        System.out.println(CURRENT_ACCOUNT.driverState);
        System.out.println(CURRENT_ACCOUNT.trailerLicense);
        System.out.println(CURRENT_ACCOUNT.trailerState);
        System.out.println(CURRENT_ACCOUNT.dispatcherPhoneNumber);

         */
    }
}
