package com.dbc.kiosk.Webservices;

import android.app.Activity;
import android.os.AsyncTask;
import com.dbc.kiosk.Account;
import com.dbc.kiosk.Screens.LoggedIn;
import com.dbc.kiosk.Screens.MainActivity;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.lang.ref.WeakReference;

/**
 * CheckForExistingAccount.java
 *
 * @params Activity activity, String userID, int email_phone, boolean newAccount
 *
 * userId is either an email or a phone number
 *
 * email_phone is telling this class what userID is (0 is email, 1 is phone number)
 *
 * newAccount variable will change depending on if MainActivity is logging in
 * an existing user or the user is creating a new account.
 *
 * This uses the GetShippingTruckDriver web service method to check
 * if a given email or phone number is already registered with another
 * shipping truck driver account.
 *
 * This class was created separate from GetShippingTruckDriver.java even though
 * they use the same web service because the purpose of this class is only to
 * find out if an email and/or phone number is already in use. The purpose of the
 * other class is to receive all information about a specified shipping truck driver.
 */
public class CheckForExistingAccount extends AsyncTask<Void, Void, Void> {

    private WeakReference<Activity> mWeakActivity;
    private String userID;
    private int email_phone;
    private boolean newAccount;

    private String retrievedEmail;
    private String retrievedPhone;

    private boolean connection = false;
    private int propertyCount;

    public CheckForExistingAccount(Activity activity, String userID, int email_phone, boolean newAccount) {
        mWeakActivity = new WeakReference<>(activity);
        this.userID = userID;
        this.email_phone = email_phone;
        this.newAccount = newAccount;
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
            if (newAccount) {
                if (propertyCount > 0 && email_phone == 0) {
                    System.out.println("The email is already in use.");
                    MainActivity.emailListener.setValue(false);
                } else if (propertyCount > 0 && email_phone == 1) {
                    System.out.println("The phone number is already in use.");
                    MainActivity.phoneListener.setValue(false);
                } else if (propertyCount <= 0 && email_phone == 0) {
                    System.out.println("The email is not in use.");
                    MainActivity.emailListener.setValue(true);
                } else if (propertyCount <= 0 && email_phone == 1) {
                    System.out.println("The phone number is not in use.");
                    MainActivity.phoneListener.setValue(true);
                }
            } else {
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
}
