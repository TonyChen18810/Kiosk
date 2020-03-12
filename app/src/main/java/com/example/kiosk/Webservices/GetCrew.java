package com.example.kiosk.Webservices;

import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

public class GetCrew extends AsyncTask<Void, Void, String> {

    private static String result;

    static String getResult() {
        return result;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "GetCrews";
        String soapAction = "http://tempuri.org/GetCrews";
        String URL = "http://green.darrigo.com/DBCWebService/DBCWebService.asmx";

        SoapObject soap = new SoapObject(namespace, method);
        SoapSerializationEnvelope sEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sEnvelope.dotNet = true;
        sEnvelope.setOutputSoapObject(soap);
        HttpTransportSE transport = new HttpTransportSE(URL);
        try {
            transport.call(soapAction, sEnvelope);
            SoapObject response = (SoapObject) sEnvelope.getResponse();
            for (int i = 0; i < response.getPropertyCount(); i++) {
                System.out.println(response.getProperty(i).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
