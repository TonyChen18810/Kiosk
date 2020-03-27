package com.example.kiosk.Webservices;

import android.app.Activity;

import com.example.kiosk.Account;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.lang.ref.WeakReference;

public class GetShippingTruckDriverThread {

    private WeakReference<Activity> mWeakActivity;
    private String enteredEmail,  enteredPhoneNumber;;

    public GetShippingTruckDriverThread(Activity activity, String enteredEmail, String enteredPhoneNumber) {
        mWeakActivity = new WeakReference<>(activity);
        this.enteredEmail = enteredEmail;
        this.enteredPhoneNumber = enteredPhoneNumber;
    }

    private static String email = "", driverName = "", phone = "", truckName = "", truckNumber = "", driversLicense = "", driversLicenseState = "",
            trailerLicense = "", trailerLicenseState = "", dispatcherPhone = "", languagePreference = "", communicationPreference = "";

    public boolean call() {
        String namespace = "http://tempuri.org/";
        String method = "GetShippingTruckDriver";
        String soapAction = "http://tempuri.org/GetShippingTruckDriver";
        // String URL = "http://vmiis/DBCWebService/DBCWebService.asmx";
        String URL = "http://VMSQLTEST/DBCWebService/DBCWebService.asmx";

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("inEmail", enteredEmail);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transportSE = new HttpTransportSE(URL);

        try {
            transportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            if (response.getPropertyCount() > 0) {
                // really don't need all this, just make the object?
                email = ((SoapObject) (response.getProperty(0))).getProperty(0).toString();
                driverName = ((SoapObject) (response.getProperty(0))).getProperty(1).toString();
                phone = ((SoapObject) (response.getProperty(0))).getProperty(2).toString();
                truckName = ((SoapObject) (response.getProperty(0))).getProperty(3).toString();
                truckNumber = ((SoapObject) (response.getProperty(0))).getProperty(4).toString();
                driversLicense = ((SoapObject) (response.getProperty(0))).getProperty(5).toString();
                driversLicenseState = ((SoapObject) (response.getProperty(0))).getProperty(6).toString();
                trailerLicense = ((SoapObject) (response.getProperty(0))).getProperty(7).toString();
                trailerLicenseState = ((SoapObject) (response.getProperty(0))).getProperty(8).toString();
                dispatcherPhone = ((SoapObject) (response.getProperty(0))).getProperty(9).toString();
                languagePreference = ((SoapObject) (response.getProperty(0))).getProperty(10).toString();
                communicationPreference = ((SoapObject) (response.getProperty(0))).getProperty(11).toString();
                if (enteredEmail.toLowerCase().equals(email.toLowerCase()) && enteredPhoneNumber.equals(phone)) {
                    Account account = new Account(email, driverName, phone, truckName, truckNumber, driversLicense,
                            driversLicenseState, trailerLicense, trailerLicenseState, dispatcherPhone, languagePreference, communicationPreference);
                    Account.setCurrentAccount(account);
                    return true;
                } else {
                    return false;
                }
            } else {
                Account account = new Account(email, driverName, phone, truckName, truckNumber, driversLicense,
                        driversLicenseState, trailerLicense, trailerLicenseState, dispatcherPhone, languagePreference, communicationPreference);
                Account.setCurrentAccount(account);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Account account = new Account(null, null, null, null, null, null,
                    null, null, null, null, null, null);
            Account.setCurrentAccount(account);
            return false;
        }
    }
}
