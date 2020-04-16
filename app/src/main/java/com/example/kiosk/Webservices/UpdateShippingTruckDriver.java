package com.example.kiosk.Webservices;

import android.os.AsyncTask;
import com.example.kiosk.Account;
import com.example.kiosk.Settings;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Date;

/**
 * UpdateShippingTruckDriver.java
 *
 * @param Account account
 *
 * Uses "UpdateShippingTruckDriver" web service to either create a new truck
 * driver entry or update an existing one
 *
 * Called from either CreateAccount.java or LoggedIn.java after pressing "Next" button
 *
 * Creates/Updates user account information
 */
public class UpdateShippingTruckDriver extends AsyncTask<Void, Void, Void> {

    private Account account;

    public UpdateShippingTruckDriver(Account account) {
        this.account = account;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "UpdateShippingTruckDriver";
        String soapAction = "http://tempuri.org/UpdateShippingTruckDriver";
        // String URL = "http://vmiis/DBCWebService/DBCWebService.asmx";
        String URL = "http://VMSQLTEST/DBCWebService/DBCWebService.asmx";

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("inOldEmail", account.getEmail());
        request.addProperty("inNewEmail", account.getEmail());
        request.addProperty("inDriverName", account.getDriverName());
        request.addProperty("inDriverPhone", account.getPhoneNumber());
        request.addProperty("inTruckName", account.getTruckName());
        request.addProperty("inTruckNumber", account.getTruckNumber());
        request.addProperty("inDriversLicense", account.getDriverLicense());
        request.addProperty("inDriversLicenseState", account.getDriverState());
        request.addProperty("inTrailerLicense", account.getTrailerLicense());
        request.addProperty("inTrailerLicenseState", account.getTrailerState());
        request.addProperty("inDispatcherPhone", account.getDispatcherPhoneNumber());
        request.addProperty("inLanguagePreference", account.getLanguagePreference());
        request.addProperty("inContactPreference", account.getCommunicationPreference());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transportSE = new HttpTransportSE(URL);

        // this returns a 0 or non-zero... no account info being returned here
        try {
            transportSE.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

            if (response.toString().equals("0")) {
                System.out.println("Success, account created/updated");
            } else {
                System.out.println("Failure, account was not created/updated");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Trying again...");
            // Settings.setError(e.toString(), getClass().toString(), new Date().toString(), null);
            Thread thread = new Thread(() -> {
                new UpdateShippingTruckDriver(account).execute();
            });
            try {
                thread.start();
                Thread.sleep(3000);
            } catch (Exception ex) {
                ex.printStackTrace();
                // Settings.setError(ex.toString(), getClass().toString(), new Date().toString(), null);
            }
        }
        return null;
    }
}