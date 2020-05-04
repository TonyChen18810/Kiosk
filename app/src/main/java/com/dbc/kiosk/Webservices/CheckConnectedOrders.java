package com.dbc.kiosk.Webservices;

import android.os.AsyncTask;
import com.dbc.kiosk.Screens.OrderEntry;
import com.dbc.kiosk.Settings;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * CheckConnectedOrders.java
 *
 * Uses the GetOrderDetailsByMasterNumber web service, sends in either an email
 * address or master number and returns a list of orders with that master number
 * or email address (checked-in by that email/user)
 *
 * This is originally called from GetOrderDetails.java after that class receives
 * info about the entered order. This is called with boolean b set to true
 * which first sends in the master number of the entered order to retrieve any
 * orders connected to it. (See line 79-83)
 * We iterate through the retrieved orders, if any have isCheckedIn == true, we save
 * the master number of that order in checkedInMasterNumber.
 * This class is then called again with boolean b set to false which sends in the
 * users email to retrieve any orders linked to that user (orders they have already
 * checked in and the orders linked to those orders by master number) we save the master
 * number of any of the returned orders into driverMasterNumber.
 *
 * We know compare both master numbers, if they're equal then this driver is allowed
 * to check-in the currently entered order and any that may be connected to it (because
 * the master number/confirmation number belong to them). If the user has no master number,
 * but the order does - then he cannot check it in (if it has a master number and is checked-in,
 * that means someone else has the confirmation number).
 *
 * These checks all happen in the onPostExecute() of this class.
 *
 * Note: this situation will rarely happen.
 */
public class CheckConnectedOrders extends AsyncTask<Void, Void, Void> {

    private String inEmail;
    private String inMasterNumber;
    private int propertyCount;
    private boolean b;

    private static String driverMasterNumber = null;
    private static String checkedInMasterNumber = null;

    CheckConnectedOrders(String inEmail, String inMasterNumber, boolean b) {
        this.inEmail = inEmail;
        this.inMasterNumber = inMasterNumber;
        this.b = b;
    }

    private static void setDriverMasterNumber(String masterNumber) {
        driverMasterNumber = masterNumber;
    }

    private static void setCheckedInMasterNumber(String masterNumber) {
        checkedInMasterNumber = masterNumber;
    }

    private static String getDriverMasterNumber() {
        return driverMasterNumber;
    }

    private static String getCheckedInMasterNumber() {
        return checkedInMasterNumber;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "GetOrderDetailsByMasterNumber";
        String soapAction = "http://tempuri.org/GetOrderDetailsByMasterNumber";
        // String URL = "http://vmiis/DBCWebService/DBCWebService.asmx";
        // String URL = "http://VMSQLTEST/DBCWebService/DBCWebService.asmx";
        String URL = Settings.getDbcUrl();

        SoapObject request = new SoapObject(namespace, method);
        if (b) {
            request.addProperty("inMasterNumber", inMasterNumber);
        } else {
            request.addProperty("inMasterNumber", inEmail);
        }

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transportSE = new HttpTransportSE(URL);

        try {
            transportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            propertyCount = response.getPropertyCount();
            if (propertyCount > 0 && b)  {
                for (int i = 0; i < response.getPropertyCount(); i++) {
                    String SOPNumber = ((SoapObject) (response.getProperty(i))).getProperty(1).toString();
                    String masterNumber = ((SoapObject) (response.getProperty(i))).getProperty(0).toString();
                    String isCheckedIn = ((SoapObject) (response.getProperty(i))).getProperty(7).toString();
                    if (isCheckedIn.equals("true")) {
                        System.out.println("Checked-in is true for connected order number: " + SOPNumber);
                        setCheckedInMasterNumber(masterNumber);
                        break;
                    }
                }
            } else if (propertyCount > 0 && !b) {
                String masterNumber = ((SoapObject) (response.getProperty(0))).getProperty(0).toString();
                setDriverMasterNumber(masterNumber);
            } else if (propertyCount < 1 && !b) {
                setDriverMasterNumber(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (b) {
            new CheckConnectedOrders(inEmail, inMasterNumber, false).execute();
        } else {
            if (getCheckedInMasterNumber() == null && getDriverMasterNumber() == null) {
                System.out.println("Both are null, setting to 1 (valid)");
                OrderEntry.validOrderNumber.setValue(1);
                setDriverMasterNumber(null);
                setCheckedInMasterNumber(null);
            } else if (getCheckedInMasterNumber() == null && getDriverMasterNumber() != null) {
                OrderEntry.validOrderNumber.setValue(1);
                System.out.println("Checked in order is null, but driver is not null - setting to 1 (valid)");
                setDriverMasterNumber(null);
                setCheckedInMasterNumber(null);
            } else if (getCheckedInMasterNumber() != null && getDriverMasterNumber() == null) {
                System.out.println("Checked in order is not null, driver is null - setting to 5 (invalid)");
                OrderEntry.validOrderNumber.setValue(5);
                setDriverMasterNumber(null);
                setCheckedInMasterNumber(null);
            } else if (getCheckedInMasterNumber().equals(getDriverMasterNumber())) {
                System.out.println("The checked in order master number is the same as the drivers! setting to 1 (valid)");
                OrderEntry.validOrderNumber.setValue(1);
                setDriverMasterNumber(null);
                setCheckedInMasterNumber(null);
            } else {
                System.out.println("Else statement, set to 5 (invalid)");
                OrderEntry.validOrderNumber.setValue(5);
                setDriverMasterNumber(null);
                setCheckedInMasterNumber(null);
            }
        }
    }
}
