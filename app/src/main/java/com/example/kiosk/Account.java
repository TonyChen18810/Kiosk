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

    private static ArrayList<Account> accounts = new ArrayList<>();

    Account(String email, String phoneNumber, String truckName, String truckNumber,
                   String trailerLicense, String trailerState, String driverLicense,
                   String driverState, String driverName, String dispatcherPhoneNumber) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.truckName = truckName;
        this.truckNumber = truckNumber;
        this.trailerLicense = trailerLicense;
        this.trailerState = trailerState;
        this.driverLicense = driverLicense;
        this.driverState = driverState;
        this.driverName = driverName;
        this.dispatcherPhoneNumber = dispatcherPhoneNumber;
    }

    public String getEmail() {
        return email;
    }

    String getPhoneNumber() {
        return phoneNumber;
    }

    String getTruckName() {
        return truckName;
    }

    String getTruckNumber() {
        return truckNumber;
    }

    String getTrailerLicense() { return trailerLicense; }

    String getTrailerState() { return trailerState; }

    String getDriverLicense() {
        return driverLicense;
    }

    String getDriverState() { return driverState; }

    String getDriverName() {
        return driverName;
    }

    String getDispatcherPhoneNumber() {
        return dispatcherPhoneNumber;
    }

    static ArrayList<Account> getAccounts() {
        return accounts;
    }

    static void addAccount(Account account) {
        accounts.add(account);
    }

    static void clearAccounts() {
        accounts.clear();
    }
}
