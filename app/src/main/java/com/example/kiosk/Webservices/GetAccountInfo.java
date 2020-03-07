package com.example.kiosk.Webservices;

import android.app.Activity;
import android.os.AsyncTask;

import com.example.kiosk.Account;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.lang.ref.WeakReference;

public class GetAccountInfo extends AsyncTask<Void, Void, Account> {

    private static String truckerID;
    private static Account result;
    private WeakReference<Activity> mWeakActivity;

    public GetAccountInfo(Activity activity) {
        mWeakActivity = new WeakReference<Activity>(activity);

    }

    static Account getResult() {
        return result;
    }

    static void setTruckerID(String ID) {
        truckerID = ID;
    }

    @Override
    protected Account doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "GetTruckDrivers";
        String soapAction = "http://tempuri.org/GetTruckDrivers";
        String URL = "http://green.darrigo.com/DBCWebService/DBCWebService.asmx";

        SoapObject soap = new SoapObject(namespace, method);
        SoapSerializationEnvelope sEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sEnvelope.dotNet = true;
        sEnvelope.setOutputSoapObject(soap);
        HttpTransportSE transport = new HttpTransportSE(URL);

        try {
            transport.call(soapAction, sEnvelope);
            SoapObject response = (SoapObject) sEnvelope.getResponse();

            for (int i = 0; i < response.getPropertyCount(); i++) {
                if (((SoapObject) (response.getProperty(i))).getProperty(0).toString().equals(truckerID)) {
                    // result = new TruckerTestAccount(((SoapObject) (response.getProperty(i))).getProperty(0).toString(), ((SoapObject) (response.getProperty(i))).getProperty(1).toString(),
                            // ((SoapObject) (response.getProperty(i))).getProperty(2).toString(), ((SoapObject) (response.getProperty(i))).getProperty(3).toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Account account) {
        super.onPostExecute(account);
        // dataSendToActivity.sendData(account);
        Activity activity = mWeakActivity.get();
        if (activity != null) {
            Account.setCurrentAccount(account);
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