package com.example.kiosk.Webservices;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import com.example.kiosk.Account;
import com.example.kiosk.R;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.lang.ref.WeakReference;

public class UpdateShippingTruckDriver extends AsyncTask<Void, Void, Void> {

    private WeakReference<Activity> mWeakActivity;
    private String email, driverName, phone, truckName, truckNumber, driversLicense,
            driversLicenseState, trailerLicense, trailerLicenseState, dispatcherPhone;

    public UpdateShippingTruckDriver(Activity activity, String email, String driverName, String phone, String truckName, String truckNumber, String driversLicense,
                                     String driversLicenseState, String trailerLicense, String trailerLicenseState, String dispatcherPhone) {
        mWeakActivity = new WeakReference<Activity>(activity);
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
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "UpdateShippingTruckDriver";
        String soapAction = "http://tempuri.org/UpdateShippingTruckDriver";
        String URL = "http://vmiis/DBCWebService/DBCWebService.asmx";

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("inOldEmail", email);
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

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transportSE = new HttpTransportSE(URL);

        try {
            transportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();

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
                Account account = new Account(email, driverName, phone, truckName, truckNumber, driversLicense,
                        driversLicenseState, trailerLicense, trailerLicenseState, dispatcherPhone);
                Account.setCurrentAccount(account);
            } else {
                Account account = new Account(email, driverName, phone, truckName, truckNumber, driversLicense,
                        driversLicenseState, trailerLicense, trailerLicenseState, dispatcherPhone);
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
