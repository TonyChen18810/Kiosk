package com.example.kiosk.Webservices;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import com.example.kiosk.MasterOrder;
import com.example.kiosk.Screens.OrderEntry;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class GetOrderDetailsByMasterNumber extends AsyncTask<Void, Void, Void> {

    private String inMasterNumber;
    private static int propertyCount;

    public GetOrderDetailsByMasterNumber(String inMasterNumber) {
        this.inMasterNumber = inMasterNumber;
    }

    public static int getPropertyCount() {
        return propertyCount;
    }

    @SuppressLint("WrongThread")
    @Override
    protected Void doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "GetOrderDetailsByMasterNumber";
        String soapAction = "http://tempuri.org/GetOrderDetailsByMasterNumber";
        String URL = "http://vmiis/DBCWebService/DBCWebService.asmx";

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("inMasterNumber", inMasterNumber);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transportSE = new HttpTransportSE(URL);

        try {
            transportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            propertyCount = response.getPropertyCount();
            System.out.println(propertyCount);
            if (propertyCount < 1) {
                // empty list, no associated orders
                System.out.println("No orders with matching master number (" + inMasterNumber + ") ----------------------------------------------, property count: " + propertyCount);
            } else {
                for (int i = 0; i < response.getPropertyCount(); i++) {
                    String masterNumber = ((SoapObject) (response.getProperty(i))).getProperty(0).toString();
                    String SOPNumber = ((SoapObject) (response.getProperty(i))).getProperty(1).toString();
                    String coolerLocation = ((SoapObject) (response.getProperty(i))).getProperty(2).toString();
                    String destination = ((SoapObject) (response.getProperty(i))).getProperty(3).toString();
                    String consignee = ((SoapObject) (response.getProperty(i))).getProperty(4).toString();
                    String truckStatus = ((SoapObject) (response.getProperty(i))).getProperty(5).toString();
                    String customerName = ((SoapObject) (response.getProperty(i))).getProperty(6).toString();
                    String isCheckedIn = ((SoapObject) (response.getProperty(i))).getProperty(7).toString();
                    String isAppointment = ((SoapObject) (response.getProperty(i))).getProperty(8).toString();
                    String orderDate = ((SoapObject) (response.getProperty(i))).getProperty(9).toString();
                    String appointmentTime = ((SoapObject) (response.getProperty(i))).getProperty(10).toString();
                    String estimatedWeight = ((SoapObject) (response.getProperty(i))).getProperty(11).toString();
                    String estimatedPallets = ((SoapObject) (response.getProperty(i))).getProperty(12).toString();
                    boolean canBeInserted = true;
                    for (int j = 0; j < MasterOrder.getMasterOrdersList().size(); j++) {
                        if (MasterOrder.getMasterOrdersList().get(j).getSOPNumber().equals(SOPNumber)) {
                            canBeInserted = false;
                        }
                    }
                    for (int k = 0; k < MasterOrder.getPossibleMasterOrdersList().size(); k++) {
                        if (MasterOrder.getPossibleMasterOrdersList().get(k).getSOPNumber().equals(SOPNumber)) {
                            canBeInserted = false;
                        }
                    }

                    for (int l = 0; l < MasterOrder.getAssociatedMasterOrdersList().size(); l++) {
                        if (MasterOrder.getAssociatedMasterOrdersList().get(l).getSOPNumber().equals(SOPNumber)) {
                            canBeInserted = false;
                        }
                    }

                    if (canBeInserted) {
                        MasterOrder masterOrder = new MasterOrder(masterNumber, SOPNumber, coolerLocation, destination, consignee, truckStatus,
                                customerName, isCheckedIn, isAppointment, orderDate, appointmentTime, estimatedWeight, estimatedPallets);
                        MasterOrder.addAssociatedMasterOrderToList(masterOrder);
                    }
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
        if (propertyCount < 1 && MasterOrder.getAssociatedMasterOrdersList().size() < 1) {
            System.out.println("No associated orders");
            OrderEntry.sharedMasterNumber.setValue(false);
        } else if (MasterOrder.getAssociatedMasterOrdersList().size() > 0){
            System.out.println("There's associated orders!!");
            OrderEntry.sharedMasterNumber.setValue(true);
        }

    }
}
