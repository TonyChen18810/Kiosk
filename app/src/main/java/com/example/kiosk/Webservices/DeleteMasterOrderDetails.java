package com.example.kiosk.Webservices;

import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class DeleteMasterOrderDetails extends AsyncTask<Void, Void, Void> {

    private String SOPnumber;

    DeleteMasterOrderDetails(String SOPnumber) {
        this.SOPnumber = SOPnumber;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "DeleteMasterOrderDetails";
        String soapAction = "http://tempuri.org/DeleteMasterOrderDetails";
        String URL = "http://vmiis/DBCWebService/DBCWebService.asmx";

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
        }

        return null;
    }
}
