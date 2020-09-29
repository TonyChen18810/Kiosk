package com.dbc.kiosk.Webservices;

import android.os.AsyncTask;
import com.dbc.kiosk.Settings;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class GetSystemPassword extends AsyncTask<Void, Void, String> {
    private String systemPassword;
    private boolean connection;

    public GetSystemPassword() {
        this.connection = false;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "GetSystemPassword";
        String soapAction = "http://tempuri.org/GetSystemPassword";
        String URL = Settings.getDbcUrl();

        SoapObject request = new SoapObject(namespace, method);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transportSE = new HttpTransportSE(URL);

        try {
            transportSE.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            if (response != null) {
                this.connection = true;
                response.getValue();
                this.systemPassword = (String) response.getValue();
                return(this.systemPassword);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
