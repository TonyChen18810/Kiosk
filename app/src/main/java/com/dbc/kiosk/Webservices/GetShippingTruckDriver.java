package com.dbc.kiosk.Webservices;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import com.dbc.kiosk.Account;
import com.dbc.kiosk.R;
import com.dbc.kiosk.Screens.MainActivity;
import com.dbc.kiosk.Settings;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.lang.ref.WeakReference;
import java.util.Date;
/**
 * GetShippingTruckDriver.java
 *
 * @param Activity activity, String enteredEmail
 *
 * Uses "GetShippingTruckDriver" web service to get information of a truck driven
 * given the provided email (enteredEmail)
 *
 * Called from MainActivity.java when the "Next" button is pressed
 */
public class GetShippingTruckDriver extends AsyncTask<Void, Void, Void> {

    private WeakReference<Activity> mWeakActivity;
    private String inEmail;

    private boolean connection = false;

    public GetShippingTruckDriver(Activity activity, String inEmail) {
        mWeakActivity = new WeakReference<>(activity);
        this.inEmail = inEmail;
    }

    private static String email = "", driverName = "", phone = "", truckName = "", truckNumber = "", driversLicense = "", driversLicenseState = "",
            trailerLicense = "", trailerLicenseState = "", dispatcherPhone = "", languagePreference = "", communicationPreference = "";

    static public String getEmail() {
        return email;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "GetShippingTruckDriver";
        String soapAction = "http://tempuri.org/GetShippingTruckDriver";
        // String URL = "http://vmiis/DBCWebService/DBCWebService.asmx";
        String URL = "http://VMSQLTEST/DBCWebService/DBCWebService.asmx";

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("inEmail", inEmail);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transportSE = new HttpTransportSE(URL);

        try {
            transportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            if (response != null) {
                connection = true;
                if (response.getPropertyCount() > 0) {
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
                    Account account = new Account(email, driverName, phone, truckName, truckNumber, driversLicense,
                        driversLicenseState, trailerLicense, trailerLicenseState, dispatcherPhone, languagePreference, communicationPreference);
                    Account.setCurrentAccount(account);
                }
            } else {
                Account account = new Account(email, driverName, phone, truckName, truckNumber, driversLicense,
                        driversLicenseState, trailerLicense, trailerLicenseState, dispatcherPhone, languagePreference, communicationPreference);
                Account.setCurrentAccount(account);
            }
        } catch (Exception e) {
            e.printStackTrace();
            connection = false;
            Account account = new Account(null, null, null, null, null, null,
                    null, null, null, null, null, null);
            Account.setCurrentAccount(account);
            System.out.println("Trying again...");
            Thread thread = new Thread(() -> {
                new GetShippingTruckDriver(mWeakActivity.get(), inEmail).execute();
            });
            thread.start();
            try {
                Thread.sleep(3000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (connection) {
            Activity activity = mWeakActivity.get();
            if (activity != null) {
                ProgressBar progressBar = activity.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);
            }
            if (getEmail().length() == 0) {
                MainActivity.accountExists.setValue(false);
            } else {
                MainActivity.accountExists.setValue(true);
            }
        }
        // reset in case of different login attempt
        email = "";
        driverName = "";
        phone = "";
        truckName = "";
        truckNumber = "";
        driversLicense = "";
        driversLicenseState = "";
        trailerLicense = "";
        trailerLicenseState = "";
        dispatcherPhone = "";
    }
}