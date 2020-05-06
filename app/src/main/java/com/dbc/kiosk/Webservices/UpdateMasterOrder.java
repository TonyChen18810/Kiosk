package com.dbc.kiosk.Webservices;

import android.app.Activity;
import android.os.AsyncTask;

import com.dbc.kiosk.Settings;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.lang.ref.WeakReference;

/**
 * UpdateMasterOrder.java
 *
 * @param String inMasterNumber, String inEmail, String inSOPnumber, String isCheckedIn, boolean lastCall
 *
 * Uses "UpdateMasterOrder" web service to connect provided order number (inSOPnumber)
 * to provided master number (inMasterNumber) and sets the "isCheckedIn" value of the order to true
 *
 * Called from OrderSummary.java after pressing "Confirm"
 *
 * Checks in an order
 */
public class UpdateMasterOrder extends AsyncTask<Void, Void, Void> {

    private String inMasterNumber;
    private String inEmail;
    private String inComment = "";
    private String inUserID = "Kiosk";
    private String inSOPnumber;
    private String inLocation = "01";
    private String isCheckedIn;
    private boolean lastCall;

    private WeakReference<Activity> mWeakReference;

    private boolean connection = false;

    public UpdateMasterOrder(String inMasterNumber, String inEmail, String inSOPnumber, Activity activity, String isCheckedIn, boolean lastCall) {
        this.inMasterNumber = inMasterNumber;
        this.inEmail = inEmail;
        this.inSOPnumber = inSOPnumber;
        this.isCheckedIn = isCheckedIn;
        this.lastCall = lastCall;
        mWeakReference = new WeakReference<>(activity);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "UpdateMasterOrder";
        String soapAction = "http://tempuri.org/UpdateMasterOrder";
        // String URL = "http://vmiis/DBCWebService/DBCWebService.asmx";
        // String URL = "http://VMSQLTEST/DBCWebService/DBCWebService.asmx";
        String URL = Settings.getDbcUrl();

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("inMasterNumber", inMasterNumber);
        request.addProperty("inEmail", inEmail);
        request.addProperty("inComment", inComment);
        request.addProperty("inUserID", Settings.getKioskNumber());
        request.addProperty("inSopNumber", inSOPnumber);
        request.addProperty("inLocation", Settings.getCoolerLocation());
        request.addProperty("inCheckedIn", isCheckedIn);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transportSE = new HttpTransportSE(URL);

        try {
            transportSE.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            if (response != null) {
                connection = true;
            }
            if (response.toString().equals("0")) {
                System.out.println("UpdateMasterOrder success");
            } else if (response.toString().equals("1000")) {
                System.out.println("Already given another master number");
            } else if (response.toString().equals("1010")) {
                System.out.println("Invoiced and moved to history");
            }
        } catch (Exception e) {
            e.printStackTrace();
            connection = false;
            System.out.println("Trying again...");
            // Settings.setError(e.toString(), getClass().toString(), new Date().toString(),null);
            Thread thread = new Thread(() -> {
                new UpdateMasterOrder(inMasterNumber, inEmail, inSOPnumber, mWeakReference.get(), isCheckedIn, lastCall).execute();
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
            if (this.lastCall) {
                new DriverNotification(GetOrderDetails.getMasterNumber(), mWeakReference.get()).execute();
            }
        }
    }
}
