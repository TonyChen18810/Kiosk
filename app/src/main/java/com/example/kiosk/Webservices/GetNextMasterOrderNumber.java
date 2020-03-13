package com.example.kiosk.Webservices;

import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class GetNextMasterOrderNumber extends AsyncTask<Void, Void, Void> {

    private static String nextMasterNumber;

    @Override
    protected Void doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "GetNextMasterOrderNumber";
        String soapAction = "http://tempuri.org/GetNextMasterOrderNumber";
        String URL = "http://vmiis/DBCWebService/DBCWebService.asmx";

        SoapObject request = new SoapObject(namespace, method);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transportSE = new HttpTransportSE(URL);

        try {
            transportSE.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            nextMasterNumber = response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        GetMasterOrderDetails.setNewMasterNumber(nextMasterNumber);
    }
}
