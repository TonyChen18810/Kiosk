package com.example.kiosk.Webservices;

import android.os.AsyncTask;
import com.example.kiosk.Helpers.Time;
import com.example.kiosk.Settings;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
/**
 * GetServerTime.java
 *
 * Uses "GetServerTime" web service to retrieve the server time
 * at which the user logged in
 *
 * Called from LoggedIn.java, the value that is returned will later
 * be used to compare with order appointment times to determine
 * if user is on-time/late/early etc.
 */
public class GetServerTime extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "GetServerTime";
        String soapAction = "http://tempuri.org/GetServerTime";
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
            String time = response.toString().split(" ")[1];
            System.out.println("Server time without date: " + time);
            Time.setTime(time);
        } catch (Exception e) {
            e.printStackTrace();
            Settings.setError(e.toString(), getClass().toString(), null);
            Thread thread = new Thread(() -> {
                new GetServerTime().execute();
            });
            thread.start();
            try {
                Thread.sleep(3000);
            } catch (Exception ex) {
                ex.printStackTrace();
                Settings.setError(ex.toString(), getClass().toString(), null);
            }
        }
        return null;
    }
}
