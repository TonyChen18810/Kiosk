package com.example.kiosk;

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

    public Account(String email, String phoneNumber, String truckName, String truckNumber,
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getTruckName() {
        return truckName;
    }

    public String getTruckNumber() {
        return truckNumber;
    }

    public String getTrailerLicense() { return trailerLicense; }

    public String getTrailerState() { return trailerState; }

    public String getDriverLicense() {
        return driverLicense;
    }

    public String getDriverState() { return driverState; }

    public String getDriverName() {
        return driverName;
    }

    public String getDispatcherPhoneNumber() {
        return dispatcherPhoneNumber;
    }
}
