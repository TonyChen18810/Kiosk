package com.example.kiosk.Webservices;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;

import com.example.kiosk.Account;
import com.example.kiosk.Screens.MainActivity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.lang.ref.WeakReference;

public class GetShippingTruckDriver extends AsyncTask<Void, Void, String> {

    private static String accountEmail = null;

    private WeakReference<Activity> mWeakActivity;

    public GetShippingTruckDriver(Activity activity) {
        mWeakActivity = new WeakReference<Activity>(activity);
    }

    public static String getEmail() {
        return accountEmail;
    }

    @SuppressLint("WrongThread")
    @Override
    protected String doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "GetShippingTruckDriver";
        String soapAction = "http://tempuri.org/GetShippingTruckDriver";
        String URL = "http://vmiis/DBCWebService/DBCWebService.asmx";

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("inEmail", "test@gmail.com");

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transportSE = new HttpTransportSE(URL);

        try {
            // MainActivity.progressBar.setVisibility(View.VISIBLE);
            transportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();

            for (int i = 0; i < response.getPropertyCount(); i++) {
                System.out.println(response.getProperty(i));
            }
            for (int i = 0; i < response.getPropertyCount(); i++) {
                ((SoapObject) (response.getProperty(i))).getProperty(i).toString();
            }
            String email = ((SoapObject) (response.getProperty(0))).getProperty(0).toString();
            accountEmail = email;
            String driverName = ((SoapObject) (response.getProperty(0))).getProperty(1).toString();
            String phone = ((SoapObject) (response.getProperty(0))).getProperty(2).toString();
            String truckName = ((SoapObject) (response.getProperty(0))).getProperty(3).toString();
            String truckNumber = ((SoapObject) (response.getProperty(0))).getProperty(4).toString();
            String driversLicense = ((SoapObject) (response.getProperty(0))).getProperty(5).toString();
            String driversLicenseState = ((SoapObject) (response.getProperty(0))).getProperty(6).toString();
            String trailerLicense = ((SoapObject) (response.getProperty(0))).getProperty(7).toString();
            String trailerLicenseState = ((SoapObject) (response.getProperty(0))).getProperty(8).toString();
            String dispatcherPhone = ((SoapObject) (response.getProperty(0))).getProperty(9).toString();
            Account account = new Account(email, driverName, phone, truckName, truckNumber, driversLicense,
                    driversLicenseState, trailerLicense, trailerLicenseState, dispatcherPhone);
            Account.setCurrentAccount(account);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountEmail;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s == null) {
            MainActivity.accountExists.setValue(false);
        } else {
            MainActivity.accountExists.setValue(true);
        }
    }
}