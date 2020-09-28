package com.dbc.kiosk.Webservices;

import android.os.AsyncTask;
import com.dbc.kiosk.Account;
import com.dbc.kiosk.Settings;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * GetAllEmails.java
 *
 * This is used to supply EmailSuggestionAdapter.java with a
 * list of emails to create a suggestion list with
 *
 * Uses the GetShippingTruckDriver web service method but instead
 * passes in a wildcard (%) to retrieve all emails from the database
 *
 * The list of emails is maintained in Account.java
 *
 * Called from FirstScreen.java
 */
public class GetAllEmails extends AsyncTask<Void, Void, Void> {

    private boolean connection;

    @Override
    protected Void doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "GetShippingTruckDriver";
        String soapAction = "http://tempuri.org/GetShippingTruckDriver";
        String URL = Settings.getDbcUrl();

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("inEmail", "%");

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transportSE = new HttpTransportSE(URL);

        try {
            transportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            if (response != null) {
                connection = true;
                if (response.getPropertyCount() > 0) {
                    Account.setEMAIL_LIST();
                    for (int i = 0; i < response.getPropertyCount(); i++) {
                        String email = ((SoapObject) (response.getProperty(i))).getProperty(0).toString();
                        Account.addToEMAIL_LIST(email);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            connection = false;
            System.out.println("Trying again...");
            Thread thread = new Thread(() -> new GetAllEmails().execute());
            thread.start();
            try {
                Thread.sleep(3000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (!connection) {
            // System.out.println("Connection interrupted...trying GetAllEmails() again...");
            // Account.setEMAIL_LIST();
            // new GetAllEmails().execute();
        }
    }
}
