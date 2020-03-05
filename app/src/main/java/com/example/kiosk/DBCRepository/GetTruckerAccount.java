package com.example.kiosk.DBCRepository;

import android.os.AsyncTask;

import com.example.kiosk.Account;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

public class GetTruckerAccount extends AsyncTask<Void, Void, String> {

    private static String result;

    public static String getResult() {
        return result;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "GetTruckDrivers";
        String soapAction = "http://tempuri.org/GetTruckDrivers";
        String URL = "http://green.darrigo.com/DBCWebService/DBCWebService.asmx";

        SoapObject soap = new SoapObject(namespace, method);
        SoapSerializationEnvelope sEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        sEnvelope.dotNet = true;
        sEnvelope.setOutputSoapObject(soap);
        HttpTransportSE transport = new HttpTransportSE(URL);
        System.out.println("just above try/catch");

        try {
            System.out.println("we in the try area");
            transport.call(soapAction, sEnvelope);
            SoapObject response = (SoapObject) sEnvelope.getResponse();
            TruckerTestAccount account;
            for (int i = 0; i < response.getPropertyCount(); i++) {
                /*
                account = new TruckerTestAccount(((SoapObject) (response.getProperty(i))).getProperty(0).toString(),
                        ((SoapObject) (response.getProperty(i))).getProperty(1).toString(), ((SoapObject) (response.getProperty(i))).getProperty(2).toString(),
                        ((SoapObject) (response.getProperty(i))).getProperty(3).toString());
                result.add(account);

                 */
            }
            result = ((SoapObject) (response.getProperty(0))).getProperty(0).toString();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("we in the catch...");
        }
        return result;
    }
}
