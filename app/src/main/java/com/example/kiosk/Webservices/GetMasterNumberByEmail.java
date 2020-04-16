package com.example.kiosk.Webservices;

import android.os.AsyncTask;
import com.example.kiosk.Settings;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Date;

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
 * Called from LoggedIn.java
 */
public class GetMasterNumberByEmail extends AsyncTask<Void, Void, Void> {

    private String email;

    public GetMasterNumberByEmail(String email) {
        this.email = email;
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
                System.out.println("User does not have a master number already");
            } else {
                System.out.println("User has a master number...");
                String masterNumber = ((SoapObject) (response.getProperty(0))).getProperty(0).toString();
                GetOrderDetails.setNewMasterNumber(masterNumber);
                System.out.println("The user's master number is: " + GetOrderDetails.getMasterNumber());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Trying again...");
            // Settings.setError(e.toString(), getClass().toString(), new Date().toString(),null);
            Thread thread = new Thread(() -> {
                new GetMasterNumberByEmail(email).execute();
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
}
