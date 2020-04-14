package com.example.kiosk.Webservices;

import android.os.AsyncTask;

import com.example.kiosk.Helpers.Time;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
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

    private boolean connection = false;

    public UpdateMasterOrder(String inMasterNumber, String inEmail, String inSOPnumber, String isCheckedIn, boolean lastCall) {
        this.inMasterNumber = inMasterNumber;
        this.inEmail = inEmail;
        this.inSOPnumber = inSOPnumber;
        this.isCheckedIn = isCheckedIn;
        this.lastCall = lastCall;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "UpdateMasterOrder";
        String soapAction = "http://tempuri.org/UpdateMasterOrder";
        // String URL = "http://vmiis/DBCWebService/DBCWebService.asmx";
        String URL = "http://VMSQLTEST/DBCWebService/DBCWebService.asmx";

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("inMasterNumber", inMasterNumber);
        request.addProperty("inEmail", inEmail);
        request.addProperty("inComment", inComment);
        request.addProperty("inUserID", inUserID);
        request.addProperty("inSopNumber", inSOPnumber);
        request.addProperty("inLocation", inLocation);
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
            Thread thread = new Thread(() -> {
                new UpdateMasterOrder(inMasterNumber, inEmail, inSOPnumber, isCheckedIn, lastCall).execute();
            });
            try {
                thread.start();
                // Thread.sleep(5000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (connection) {
            if (this.lastCall) {
                new DriverNotification(GetOrderDetails.getMasterNumber()).execute();
            }
        }
    }
}
