package com.example.kiosk.Webservices;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Looper;

import com.example.kiosk.Screens.CreateAccount;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.lang.ref.WeakReference;

public class UpdateShippingTruckDriver extends AsyncTask<Void, Void, Void> {

    private WeakReference<Activity> mWeakActivity;
    private String oldEmail, email, driverName, phone, truckName, truckNumber, driversLicense,
            driversLicenseState, trailerLicense, trailerLicenseState, dispatcherPhone, languagePreference, commmunicationPreference;

    public UpdateShippingTruckDriver(Activity activity, String oldEmail, String email, String driverName, String phone, String truckName, String truckNumber, String driversLicense,
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

    @Override
    protected Void doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "UpdateShippingTruckDriver";
        String soapAction = "http://tempuri.org/UpdateShippingTruckDriver";
        String URL = "http://vmiis/DBCWebService/DBCWebService.asmx";

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
            Thread thread = new Thread(() -> {
                new UpdateShippingTruckDriver(mWeakActivity.get(), email, email, driverName, phone, truckName, truckNumber,
                        driversLicense, driversLicenseState, trailerLicense, trailerLicenseState, dispatcherPhone,"0", commmunicationPreference).execute();
            });
            try {
                thread.start();
                Thread.sleep(5000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Activity activity = mWeakActivity.get();
        if (activity != null) {
            // ProgressBar progressBar = activity.findViewById(R.id.progressBar);
            // progressBar.setVisibility(View.GONE);
        }
        // reset
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
