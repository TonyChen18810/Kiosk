package com.dbc.kiosk.Webservices;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import com.dbc.kiosk.Screens.OrderEntry;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.lang.ref.WeakReference;
/**
 * GetMasterNumberByEmail.java
 *
 * @param String email
 *
 * Uses "GetOrderDetailsByMasterNumber" web service to retrieve all orders that
 * are linked to an email by master number. This is the same web service that is
 * used in GetOrderDetailsByMasterNumber.java, but passes in an email string instead
 * of a master number string. This is used in the case a user returns to the kiosk to
 * check in additional orders, as the new orders will need to be connected with the old.
 *
 * Called from UpdateShippingTruckDriver.java
 */
public class GetMasterNumberByEmail extends AsyncTask<Void, Void, Void> {

    private String email;
    private WeakReference<Activity> mWeakActivity;

    GetMasterNumberByEmail(Activity activity, String email) {
        this.email = email;
        mWeakActivity = new WeakReference<>(activity);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "GetOrderDetailsByMasterNumber";
        String soapAction = "http://tempuri.org/GetOrderDetailsByMasterNumber";
        // String URL = "http://vmiis/DBCWebService/DBCWebService.asmx";
        String URL = "http://VMSQLTEST/DBCWebService/DBCWebService.asmx";

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("inMasterNumber", email);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transportSE = new HttpTransportSE(URL);

        try {
            transportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            if (response.getPropertyCount() < 1) {
                System.out.println("USER DOES NOT HAVE A MASTER NUMBER");
            } else {
                System.out.println("USER HAS A MASTER NUMBER...");
                String masterNumber = ((SoapObject) (response.getProperty(0))).getProperty(0).toString();
                GetOrderDetails.setNewMasterNumber(masterNumber);
                System.out.println("THE USER'S MASTER NUMBER IS " + GetOrderDetails.getMasterNumber());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Trying again...");
            // Settings.setError(e.toString(), getClass().toString(), new Date().toString(),null);
            Thread thread = new Thread(() -> {
                new GetMasterNumberByEmail(mWeakActivity.get(), email).execute();
            });
            thread.start();
            try {
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
        Activity activity = mWeakActivity.get();
        if (activity != null) {
            Intent intent = new Intent(activity, OrderEntry.class);
            activity.startActivity(intent);
        }
    }
}
