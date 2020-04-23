package com.dbc.kiosk.Webservices;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dbc.kiosk.Account;
import com.dbc.kiosk.R;
import com.dbc.kiosk.Screens.LoggedIn;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.lang.ref.WeakReference;

public class CheckForExistingAccount extends AsyncTask<Void, Void, Void> {

    private WeakReference<Activity> mWeakActivity;
    private String userID;
    private int email_phone;

    private String retrievedEmail;
    private String retrievedPhone;

    private boolean connection = false;
    private int propertyCount;

    public CheckForExistingAccount(Activity activity, String userID, int email_phone) {
        mWeakActivity = new WeakReference<>(activity);
        this.userID = userID;
        this.email_phone = email_phone;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "GetShippingTruckDriver";
        String soapAction = "http://tempuri.org/GetShippingTruckDriver";
        // String URL = "http://vmiis/DBCWebService/DBCWebService.asmx";
        String URL = "http://VMSQLTEST/DBCWebService/DBCWebService.asmx";

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("inEmail", userID);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transportSE = new HttpTransportSE(URL);

        try {
            transportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            if (response != null) {
                connection = true;
                propertyCount = response.getPropertyCount();
                if (propertyCount > 0) {
                    retrievedEmail = ((SoapObject) (response.getProperty(0))).getProperty(0).toString().toLowerCase();
                    retrievedPhone = ((SoapObject) (response.getProperty(0))).getProperty(2).toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (connection) {
            if (propertyCount > 0 && email_phone == 0 && !Account.getCurrentAccount().getEmail().toLowerCase().equals(retrievedEmail)) {
                System.out.println("The email is already in use.");
                LoggedIn.emailListener.setValue(0);
            } else if (propertyCount > 0 && email_phone == 1 && !Account.getCurrentAccount().getPhoneNumber().equals(retrievedPhone)) {
                System.out.println("The phone number is already in use.");
                LoggedIn.phoneListener.setValue(0);
            } else if (propertyCount <= 0 && email_phone == 0) {
                System.out.println("The email is not in use.");
                LoggedIn.emailListener.setValue(1);
            } else if (propertyCount <= 0 && email_phone == 1) {
                System.out.println("The phone number is not in use.");
                LoggedIn.phoneListener.setValue(1);
            }
        }
    }
}
