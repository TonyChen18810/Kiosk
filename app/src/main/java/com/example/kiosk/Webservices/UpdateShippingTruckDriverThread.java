package com.example.kiosk.Webservices;

import android.app.Activity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.lang.ref.WeakReference;

public class UpdateShippingTruckDriverThread {

    private WeakReference<Activity> mWeakActivity;
    private String oldEmail, email, driverName, phone, truckName, truckNumber, driversLicense,
            driversLicenseState, trailerLicense, trailerLicenseState, dispatcherPhone, languagePreference, commmunicationPreference;

    public UpdateShippingTruckDriverThread(Activity activity, String oldEmail, String email, String driverName, String phone, String truckName, String truckNumber, String driversLicense,
                                     String driversLicenseState, String trailerLicense, String trailerLicenseState, String dispatcherPhone, String languagePreference, String commmunicationPreference) {
        mWeakActivity = new WeakReference<>(activity);
        this.oldEmail = oldEmail;
        this.email = email;
        this.driverName = driverName;
        this.phone = phone;
        this.truckName = truckName;
        this.truckNumber = truckNumber;
        this.driversLicense = driversLicense;
        this.driversLicenseState = driversLicenseState;
        this.trailerLicense = trailerLicense;
        this.trailerLicenseState = trailerLicenseState;
        this.dispatcherPhone = dispatcherPhone;
        this.languagePreference = languagePreference;
        this.commmunicationPreference = commmunicationPreference;
    }

    public boolean call() {
        String namespace = "http://tempuri.org/";
        String method = "UpdateShippingTruckDriver";
        String soapAction = "http://tempuri.org/UpdateShippingTruckDriver";
        // String URL = "http://vmiis/DBCWebService/DBCWebService.asmx";
        String URL = "http://VMSQLTEST/DBCWebService/DBCWebService.asmx";

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("inOldEmail", oldEmail);
        request.addProperty("inNewEmail", email);
        request.addProperty("inDriverName", driverName);
        request.addProperty("inDriverPhone", phone);
        request.addProperty("inTruckName", truckName);
        request.addProperty("inTruckNumber", truckNumber);
        request.addProperty("inDriversLicense", driversLicense);
        request.addProperty("inDriversLicenseState", driversLicenseState);
        request.addProperty("inTrailerLicense", trailerLicense);
        request.addProperty("inTrailerLicenseState", trailerLicenseState);
        request.addProperty("inDispatcherPhone", dispatcherPhone);
        request.addProperty("inLanguagePreference", languagePreference);
        request.addProperty("inContactPreference", commmunicationPreference);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transportSE = new HttpTransportSE(URL);

        try {
            transportSE.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

            if (response.toString().equals("0")) {
                System.out.println("Success, account created/updated");
                return true;
            } else {
                System.out.println("Failure, account was not created/updated");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
