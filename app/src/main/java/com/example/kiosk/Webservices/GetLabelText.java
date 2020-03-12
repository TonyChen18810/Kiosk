package com.example.kiosk.Webservices;

import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class GetLabelText extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "GetLabelText";
        String soapAction = "http://tempuri.org/GetLabelText";
        String URL = "http://green.darrigo.com/DBCWebService/DBCWebService.asmx";

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("inLabelType", "1");
        request.addProperty("inDPI", "203");

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transport = new HttpTransportSE(URL);
        try {
            transport.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            System.out.println(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
