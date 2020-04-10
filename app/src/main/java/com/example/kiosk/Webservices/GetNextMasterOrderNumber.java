package com.example.kiosk.Webservices;

import android.app.Activity;
import android.os.AsyncTask;
import com.example.kiosk.Screens.OrderEntry;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.lang.ref.WeakReference;
/**
 * GetNextMasterOrderNumber.java
 *
 * @param Activity activity
 *
 * Uses "GetNextMasterOrderNumber" web service to retrieve a master number
 *
 * Called from SubmitDialog.java when "Yes" is pressed (Are you sure you want to submit these orders?)
 * This dialog is shown after the "Submit Order(s)" button is pressed in OrderEntry.java
 *
 * This is used if none of the currently added orders have a master number to use for tying the
 * orders together. This retrieves a master number to use and applies to orders via UpdateMasterOrder.java
 */
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
            // OrderEntry.setDialogListener(true);
            OrderEntry.submitDialogListener.setValue(true);
        }
    }
}
