package com.example.kiosk.Webservices;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import com.example.kiosk.R;
import com.example.kiosk.Screens.OrderEntry;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.lang.ref.WeakReference;

public class GetNextMasterOrderNumber extends AsyncTask<Void, Void, Void> {

    private static String nextMasterNumber;
    private static WeakReference<Activity> mWeakActivity;
    private static boolean connection = false;

    public GetNextMasterOrderNumber(Activity activity) {
        mWeakActivity = new WeakReference<>(activity);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "GetNextMasterOrderNumber";
        String soapAction = "http://tempuri.org/GetNextMasterOrderNumber";
        // String URL = "http://vmiis/DBCWebService/DBCWebService.asmx";
        String URL = "http://VMSQLTEST/DBCWebService/DBCWebService.asmx";

        SoapObject request = new SoapObject(namespace, method);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transportSE = new HttpTransportSE(URL);

        try {
            transportSE.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            nextMasterNumber = response.toString();
            System.out.println("nextMasterNumber: " + response.toString());
            connection = true;
        } catch (Exception e) {
            e.printStackTrace();
            connection = false;
            System.out.println("Failed retrieving new master number...trying again...");
            Thread thread = new Thread(() -> {
                new GetNextMasterOrderNumber(mWeakActivity.get()).execute();
            });
            thread.start();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (connection) {
            GetOrderDetails.setNewMasterNumber(nextMasterNumber);
            OrderEntry.setDialogListener(true);
        }
        /*
        for (int i = 0; i < Order.getPossibleOrdersList().size(); i++) {
            Order.getPossibleOrdersList().get(i).setMasterNumber(nextMasterNumber);
        }
         */
    }
}
