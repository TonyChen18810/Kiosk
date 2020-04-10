package com.example.kiosk.Webservices;

import android.os.AsyncTask;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
/**
 * DriverNotification.java
 *
 * @param String inMasterNumber
 *
 * Uses "UpdateMasterOrderNotification" web service to send the driver
 * a notification via email and/or text message regarding their submitted orders
 *
 * Called from UpdateMasterOrder.java when lastCall = true (when the program has
 * finished submitting the orders)
 */
public class DriverNotification extends AsyncTask<Void, Void, Void> {

    private String inMasterNumber;
    private String inCoolerLocation = "01";

    DriverNotification(String inMasterNumber) {
        this.inMasterNumber = inMasterNumber;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "UpdateMasterOrderNotification";
        String soapAction = "http://tempuri.org/UpdateMasterOrderNotification";
        // String URL = "http://vmiis/DBCWebService/DBCWebService.asmx";
        String URL = "http://VMSQLTEST/DBCWebService/DBCWebService.asmx";

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("inMasterNumber", inMasterNumber);
        request.addProperty("inCoolerLocation", inCoolerLocation);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transportSE = new HttpTransportSE(URL);

        try {
            transportSE.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            if (response.toString().equals("0")) {
                System.out.println("Notification successfully sent to driver");
            } else {
                System.out.println("Fail, notification not sent to driver.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
