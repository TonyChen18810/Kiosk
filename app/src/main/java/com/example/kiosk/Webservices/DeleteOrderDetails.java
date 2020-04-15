package com.example.kiosk.Webservices;

import android.os.AsyncTask;
import com.example.kiosk.Settings;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
/**
 * DeleteOrderDetails.java
 *
 * @param String SOPnumber
 *
 * Uses "DeleteMasterOrderDetails" web service to delete the master number of
 * provided SOP number
 *
 * Called from OrderSummary.java after pressing the "Confirm" button
 *
 * This is used to delete the master number of an order before giving
 * the order a new master number (possibly tying together multiple orders)
 * and checking it in
 */
public class DeleteOrderDetails extends AsyncTask<Void, Void, Void> {

    private String SOPnumber;

    public DeleteOrderDetails(String SOPnumber) {
        this.SOPnumber = SOPnumber;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "DeleteMasterOrderDetails";
        String soapAction = "http://tempuri.org/DeleteMasterOrderDetails";
        // String URL = "http://vmiis/DBCWebService/DBCWebService.asmx";
        String URL = "http://VMSQLTEST/DBCWebService/DBCWebService.asmx";

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("inSOPNumber", SOPnumber);
        request.addProperty("inCoolerLocation", "01");

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transportSE = new HttpTransportSE(URL);

        try {
            transportSE.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            if (response.toString().equals("0")) {
                System.out.println("Success, order info deleted");
            } else if (response.toString().equals("1000")) {
                System.out.println("Failure, order info NOT deleted");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Settings.setError(e.toString(), getClass().toString(), null);
            /*
            Thread thread = new Thread(() -> new DeleteOrderDetails(SOPnumber).execute());
            thread.start();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                Settings.setError(ex.toString(), getClass().toString(), null);
            }
             */
        }
        return null;
    }
}
