package com.example.kiosk;

/**
 * Account.java
 *
 * @params String email, String driverName, String phoneNumber
 *
 * Manages account information for each shipping truck driver,
 * populated using GetShippingTruckDriver.java web service
 */
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

    public Account(String email, String driverName, String phoneNumber, String truckName, String truckNumber, String driverLicense, String driverState,
                   String trailerLicense, String trailerState, String dispatcherPhoneNumber, String languagePreference, String communicationPreference) {
        if (email != null) {
            this.email = email.toLowerCase();
        } else {
            this.email = email;
        }
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

    public void setCommunicationPreference(String communicationPreference) {
        this.communicationPreference = communicationPreference;
    }

    public void setTruckName(String truckName) {
        this.truckName = truckName;
    }

    public void setTruckNumber(String truckNumber) {
        this.truckNumber = truckNumber;
    }

    public static Account getCurrentAccount() {
        return CURRENT_ACCOUNT;
    }

    public static void setCurrentAccount(Account account) {
        CURRENT_ACCOUNT = account;
    }

    public void updateCurrentInfo(String email, String driverName, String phoneNumber, String truckName, String truckNumber, String driverLicense, String driverState,
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
}
