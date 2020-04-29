package com.dbc.kiosk.Webservices;

import android.os.AsyncTask;
import com.dbc.kiosk.Screens.OrderEntry;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

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
        String URL = "http://VMSQLTEST/DBCWebService/DBCWebService.asmx";

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
