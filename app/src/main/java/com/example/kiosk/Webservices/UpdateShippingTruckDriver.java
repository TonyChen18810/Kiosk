package com.example.kiosk.Webservices;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.kiosk.Account;
import com.example.kiosk.R;
import com.example.kiosk.Screens.CreateAccount;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

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

    private WeakReference<Activity> mWeakActivity;

    private boolean connection = false;

    public UpdateShippingTruckDriver(Account account, Activity activity) {
        this.account = account;
        mWeakActivity = new WeakReference<>(activity);
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
            connection = response != null;
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
                new UpdateShippingTruckDriver(account, mWeakActivity.get()).execute();
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

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (connection) {
            Activity activity = mWeakActivity.get();
            if (activity != null) {
                System.out.println("Here's the class name: " + activity.getLocalClassName());
            }
            if (activity != null && activity.getLocalClassName().equals("Screens.CreateAccount")) {
                Button logoutBtn = activity.findViewById(R.id.LogoutBtn);
                ProgressBar progressBar = activity.findViewById(R.id.ProgressBar);
                logoutBtn.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);
                CreateAccount.accountCreatedListener.setValue(true);
                // CreateAccount.progessBar.hide();
                // CreateAccount.hideKeyboard(activity);
                // System.out.println("Here's the current focus: " + activity.getCurrentFocus());
            }
        }
    }
}