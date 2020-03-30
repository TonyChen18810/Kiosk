package com.example.kiosk.Webservices;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import com.example.kiosk.MasterOrder;
import com.example.kiosk.R;
import com.example.kiosk.Screens.OrderEntry;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.lang.ref.WeakReference;

public class GetOrderDetailsByMasterNumber extends AsyncTask<Void, Void, Void> {

    private String inMasterNumber;
    private static int propertyCount;
    private WeakReference<Activity> mWeakActivity;

    public GetOrderDetailsByMasterNumber(String inMasterNumber, Activity activity) {
        this.inMasterNumber = inMasterNumber;
        mWeakActivity = new WeakReference<>(activity);
    }

    public static int getPropertyCount() {
        return propertyCount;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "GetOrderDetailsByMasterNumber";
        String soapAction = "http://tempuri.org/GetOrderDetailsByMasterNumber";
        // String URL = "http://vmiis/DBCWebService/DBCWebService.asmx";
        String URL = "http://VMSQLTEST/DBCWebService/DBCWebService.asmx";

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
                /*
                for (int k = 0; k < response.getPropertyCount(); k++) {
                    if (((SoapObject) (response.getProperty(k))).getProperty(8).toString().equals("true")) {
                        if (!MasterOrder.getCurrentMasterOrder().getAppointmentTime().equals(((SoapObject) (response.getProperty(k))).getProperty(10).toString())) {
                            // yo these times don't match up call your dispatcher
                            String message = "";
                            if (Language.getCurrentLanguage() == 0) {
                                message = "Some of these orders have differing appointment times, please contact your dispatcher";
                            } else if (Language.getCurrentLanguage() == 1) {
                                message = "Algunos de estos pedidos tienen horarios de citas diferentes, comuníquese con su despachador";
                            } else if (Language.getCurrentLanguage() == 2) {
                                message = "Certaines de ces commandes ont des heures de rendez-vous différentes, veuillez contacter votre répartiteur";
                            }
                            HelpDialog dialog = new HelpDialog(message, mWeakActivity.get());
                            dialog.show();
                            MasterOrder.reset();
                        }
                    }
                }
                 */
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
                    for (int j = 0; j < MasterOrder.getPossibleMasterOrdersList().size(); j++) {
                        if (MasterOrder.getPossibleMasterOrdersList().get(j).getSOPNumber().equals(SOPNumber)) {
                            canBeInserted = false;
                        }
                    }

                    for (int j = 0; j < MasterOrder.getAssociatedMasterOrdersList().size(); j++) {
                        if (MasterOrder.getAssociatedMasterOrdersList().get(j).getSOPNumber().equals(SOPNumber)) {
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
        Activity activity = mWeakActivity.get();
        if (activity != null) {
            activity.findViewById(R.id.progressBar).setVisibility(View.GONE);
            // activity.findViewById(R.id.addBtn).setEnabled(true);
        }
    }
}
