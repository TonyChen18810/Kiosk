package com.example.kiosk.Webservices;

import android.os.AsyncTask;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class UpdateMasterOrder extends AsyncTask<Void, Void, Void> {

    private String inMasterNumber;
    private String inEmail;
    private String inComment = "";
    private String inUserID = "Kiosk";
    private String inSOPnumber;
    private String inLocation = "01";
    private Boolean lastCall;

    public UpdateMasterOrder(String inMasterNumber, String inEmail, String inSOPnumber, Boolean lastCall) {
        this.inMasterNumber = inMasterNumber;
        this.inEmail = inEmail;
        this.inSOPnumber = inSOPnumber;
        this.lastCall = lastCall;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "UpdateMasterOrder";
        String soapAction = "http://tempuri.org/UpdateMasterOrder";
        String URL = "http://vmiis/DBCWebService/DBCWebService.asmx";

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("inMasterNumber", inMasterNumber);
        request.addProperty("inEmail", inEmail);
        request.addProperty("inComment", inComment);
        request.addProperty("inUserID", inUserID);
        request.addProperty("inSopNumber", inSOPnumber);
        request.addProperty("inLocation", inLocation);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transportSE = new HttpTransportSE(URL);

        try {
            transportSE.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            if (response.toString().equals("0")) {
                System.out.println("UpdateMasterOrder success");
            } else if (response.toString().equals("1000")) {
                System.out.println("Already given another master number");
            } else if (response.toString().equals("1010")) {
                System.out.println("Invoiced and moved to history");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (this.lastCall) {
            new DriverNotification(GetMasterOrderDetails.getMasterNumber()).execute();
        }
    }
}
