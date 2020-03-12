package com.example.kiosk.Webservices;

import android.os.AsyncTask;

import com.example.kiosk.Screens.MainActivity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class DeleteShippingTruckDriver extends AsyncTask<Void, Void, String> {

    private static String email;
    // private WeakReference<Activity> mWeakActivity;
/*
    public GetAccountInfo(Activity activity) {
        mWeakActivity = new WeakReference<Activity>(activity);
    }
*/
    public static String getEmail() {
        return email;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "DeleteShippingTruckDriver";
        String soapAction = "http://tempuri.org/DeleteShippingTruckDriver";
        String URL = "http://green.darrigo.com/DBCWebService/DBCWebService.asmx";

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("inEmail", "test@gmail.com");

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transportSE = new HttpTransportSE(URL);

        try {
            transportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            // Object response = envelope.getResponse();
            // SoapObject response = (SoapObject) envelope.getResponse();
            // email = response.getProperty(0).toString();
            // System.out.println("Email: " + email);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return email;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String email) {
        super.onPostExecute(email);
        if (email != null) {
            if (email.length() > 0) {
                MainActivity.accountExists.setValue(true);
            } else {
                MainActivity.accountExists.setValue(false);
            }
        }
    }
}