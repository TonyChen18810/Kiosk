package com.example.kiosk.DBCRepository;

public class TruckerTestAccount {

    private String ID;
    private String firstName;
    private String middleName;
    private String lastName;

    TruckerTestAccount(String ID, String firstName, String middleName, String lastName) {
        this.ID = ID;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
    }

    public String getID() {
        return ID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }
}
