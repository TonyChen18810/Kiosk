package com.example.kiosk.Webservices;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.kiosk.Account;
import com.example.kiosk.Dialogs.HelpDialog;
import com.example.kiosk.Helpers.Language;
import com.example.kiosk.Helpers.Time;
import com.example.kiosk.MasterOrder;
import com.example.kiosk.R;
import com.example.kiosk.Screens.OrderEntry;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT;

public class GetMasterOrderDetails extends AsyncTask<Void, Void, Void> {

    private WeakReference<Activity> mWeakActivity;
    private String enteredSOPNumber;
    private static String masterNumber;
    private static String coolerNumber = "01";
    private Boolean allOrders;
    private static int propCount;

    private List<MasterOrder> masterOrderList;

    public List<MasterOrder> getMasterOrderList() {
        return masterOrderList;
    }

    public GetMasterOrderDetails(Activity activity, String enteredSOPNumber, Boolean allOrders) {
        mWeakActivity = new WeakReference<>(activity);
        this.enteredSOPNumber = enteredSOPNumber;
        this.allOrders = allOrders;
    }

    static void setNewMasterNumber(String newMasterNumber) {
        masterNumber = newMasterNumber;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "GetMasterOrderDetails";
        String soapAction = "http://tempuri.org/GetMasterOrderDetails";
        String URL = "http://vmiis/DBCWebService/DBCWebService.asmx";

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("inSOPNumber", enteredSOPNumber);
        request.addProperty("inCoolerLocation", coolerNumber);
        request.addProperty("inAllOrders", allOrders);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transportSE = new HttpTransportSE(URL);

        try {
            transportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            propCount = response.getPropertyCount();
            if (allOrders) {
                if (propCount > 0) {
                    for (int i = 0; i < response.getPropertyCount(); i++) {
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
                        MasterOrder masterOrder = new MasterOrder(masterNumber, SOPNumber, coolerLocation, destination, consignee, truckStatus,
                                customerName, isCheckedIn, isAppointment, orderDate, appointmentTime,estimatedWeight, estimatedPallets);
                        MasterOrder.addPossibleMasterOrderToList(masterOrder);
                        MasterOrder.addAssociatedMasterOrderToList(masterOrder);
                    }
                }
            } else if (response.getPropertyCount() >= 0) {
                if (((SoapObject) (response.getProperty(0))).getProperty(0) != null) {
                    masterNumber = ((SoapObject) (response.getProperty(0))).getProperty(0).toString();
                } else {
                    Thread thread = new Thread(() -> new GetNextMasterOrderNumber().execute());
                    thread.start();
                    thread.join();
                }
                masterOrderList = new ArrayList<>();
                String SOPNumber = ((SoapObject) (response.getProperty(0))).getProperty(1).toString();
                String coolerLocation = ((SoapObject) (response.getProperty(0))).getProperty(2).toString();
                String destination = ((SoapObject) (response.getProperty(0))).getProperty(3).toString();
                String consignee = ((SoapObject) (response.getProperty(0))).getProperty(4).toString();
                String truckStatus = ((SoapObject) (response.getProperty(0))).getProperty(5).toString();
                String customerName = ((SoapObject) (response.getProperty(0))).getProperty(6).toString();
                String isCheckedIn = ((SoapObject) (response.getProperty(0))).getProperty(7).toString();
                String isAppointment = ((SoapObject) (response.getProperty(0))).getProperty(8).toString();
                String orderDate = ((SoapObject) (response.getProperty(0))).getProperty(9).toString();
                String appointmentTime = ((SoapObject) (response.getProperty(0))).getProperty(10).toString();
                String estimatedWeight = ((SoapObject) (response.getProperty(0))).getProperty(11).toString();
                String estimatedPallets = ((SoapObject) (response.getProperty(0))).getProperty(12).toString();
                MasterOrder masterOrder = new MasterOrder(masterNumber, SOPNumber, coolerLocation, destination, consignee, truckStatus,
                            customerName, isCheckedIn, isAppointment, orderDate, appointmentTime,estimatedWeight, estimatedPallets);
                MasterOrder.addPossibleMasterOrderToList(masterOrder);
            } else {
                MasterOrder masterOrder = new MasterOrder("","","","",
                        "","","","","","",
                        "","","");
                // MasterOrder.addPossibleMasterOrderToList(masterOrder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Activity activity = mWeakActivity.get();
        if (activity != null) {
            ProgressBar progressBar = activity.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
        }
        System.out.println("-----------------------------------------PROPERTY COUNT: " + propCount);
        System.out.println("allOrders: " + allOrders);
        if (propCount > -1 && !allOrders) {
            if (MasterOrder.getCurrentMasterOrder().getCheckedIn().equals("true")) {
                String helpText = "";
                if (Language.getCurrentLanguage() == 0) {
                    helpText = "The order has already been checked in";
                } else if (Language.getCurrentLanguage() == 1) {
                    helpText = "El pedido ya ha sido facturado";
                } else if (Language.getCurrentLanguage() == 2) {
                    helpText = "La ordre a déjà été enregistrée";
                }
                HelpDialog dialog = new HelpDialog(helpText, activity);
                dialog.show();
            } else if (MasterOrder.getCurrentMasterOrder().getAppointment().equals("true")) {
                if (MasterOrder.getCurrentMasterOrder().getAppointmentTime().equals("00:00:00")) {
                    OrderEntry.validOrderNumber.setValue(2);
                } else {
                    OrderEntry.validOrderNumber.setValue(1);
                }
            } else {
                OrderEntry.validOrderNumber.setValue(1);
            }
        } else if (allOrders){
            EditText orderNumber = activity.findViewById(R.id.OrderNumberBox);
            orderNumber.setEnabled(true);
        } else if (propCount < 0){
            OrderEntry.validOrderNumber.setValue(0);
        }
        OrderEntry.possibleCustomerDestinations.clear();
    }
}
