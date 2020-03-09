package com.example.kiosk.Webservices;

import android.app.Activity;
import android.os.AsyncTask;
import com.example.kiosk.Screens.MainActivity;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.lang.ref.WeakReference;

public class GetAccountInfo extends AsyncTask<Void, Void, String> {

    private static String email;
    private WeakReference<Activity> mWeakActivity;

    public GetAccountInfo(Activity activity) {
        mWeakActivity = new WeakReference<Activity>(activity);

    }

    public static String getEmail() {
        return email;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "GetShippingTruckDriver";
        String soapAction = "http://tempuri.org/GetShippingTruckDriver";
        String URL = "http://green.darrigo.com/DBCWebService/DBCWebService.asmx";

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("inEmail", "test@gmail.com");
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE transport = new HttpTransportSE(URL);

        try {
            transport.call(soapAction, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();

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
                MainActivity.accountCheck.setValue(true);
            } else {
                MainActivity.accountCheck.setValue(false);
            }
        }
        // dataSendToActivity.sendData(account);
        Activity activity = mWeakActivity.get();
        if (activity != null) {
            // Account.setCurrentAccount(account);
            /*
            TextView id = activity.findViewById(R.id.ID);
            TextView first = activity.findViewById(R.id.FirstName);
            TextView middle = activity.findViewById(R.id.MiddleName);
            TextView last = activity.findViewById(R.id.LastName);
            ProgressBar progressBar = activity.findViewById(R.id.progressBar);
            id.setText(account.getID());
            first.setText(account.getFirstName());
            middle.setText(account.getMiddleName());
            last.setText(account.getLastName());
            progressBar.setVisibility(View.GONE);
             */
        }
    }
}