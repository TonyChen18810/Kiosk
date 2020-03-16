package com.example.kiosk.Webservices;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import com.example.kiosk.Account;
import com.example.kiosk.R;
import com.example.kiosk.Screens.MainActivity;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.lang.ref.WeakReference;

public class GetShippingTruckDriver extends AsyncTask<Void, Void, Void> {

    private WeakReference<Activity> mWeakActivity;
    private String enteredEmail;

    public GetShippingTruckDriver(Activity activity, String enteredEmail) {
        mWeakActivity = new WeakReference<Activity>(activity);
        this.enteredEmail = enteredEmail;
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
        String URL = "http://vmiis/DBCWebService/DBCWebService.asmx";

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("inEmail", enteredEmail);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transportSE = new HttpTransportSE(URL);

        try {
            transportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            /*
            for (int i = 0; i < response.getPropertyCount(); i++) {
                System.out.println(response.getProperty(i));
            }
            for (int i = 0; i < response.getPropertyCount(); i++) {
                ((SoapObject) (response.getProperty(i))).getProperty(i).toString();
            }
            */
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
                Account account = new Account(email, driverName, phone, truckName, truckNumber, driversLicense,
                        driversLicenseState, trailerLicense, trailerLicenseState, dispatcherPhone, languagePreference, communicationPreference);
                Account.setCurrentAccount(account);
            } else {
                Account account = new Account(email, driverName, phone, truckName, truckNumber, driversLicense,
                        driversLicenseState, trailerLicense, trailerLicenseState, dispatcherPhone, languagePreference, communicationPreference);
                Account.setCurrentAccount(account);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
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