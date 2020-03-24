package com.example.kiosk.Webservices;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.health.SystemHealthManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.example.kiosk.Dialogs.HelpDialog;
import com.example.kiosk.Helpers.Language;
import com.example.kiosk.MasterOrder;
import com.example.kiosk.R;
import com.example.kiosk.Screens.OrderEntry;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.lang.ref.WeakReference;

public class GetMasterOrderDetails extends AsyncTask<Void, Void, Void> {

    private WeakReference<Activity> mWeakActivity;
    private String enteredSOPNumber;
    private static String MASTER_NUMBER = null;
    private static String coolerNumber = "01";

    private int propCount;

    private String SOPNumber;
    private String coolerLocation;
    private String destination;
    private String consignee;
    private String truckStatus;
    private String customerName;
    private String isCheckedIn;
    private String isAppointment;
    private String orderDate;
    private String appointmentTime;
    private String estimatedWeight;
    private String estimatedPallets;

    public GetMasterOrderDetails(Activity activity, String enteredSOPNumber) {
        mWeakActivity = new WeakReference<>(activity);
        this.enteredSOPNumber = enteredSOPNumber;
        System.out.println("MASTER NUMBER ON METHOD CALL: " + MASTER_NUMBER);
    }

    public static void setNewMasterNumber(String newMasterNumber) {
        MASTER_NUMBER = newMasterNumber;
        System.out.println("New Master Number: " + MASTER_NUMBER);
     }

     public static String getMasterNumber() {
        return MASTER_NUMBER;
     }

    @SuppressLint("WrongThread")
    @Override
    protected Void doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "GetMasterOrderDetails";
        String soapAction = "http://tempuri.org/GetMasterOrderDetails";
        String URL = "http://vmiis/DBCWebService/DBCWebService.asmx";

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("inSOPNumber", enteredSOPNumber);
        request.addProperty("inCoolerLocation", coolerNumber);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transportSE = new HttpTransportSE(URL);

        try {
            transportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            propCount = response.getPropertyCount();
            if (propCount >= 0) {
                String possibleMasterNumber = ((SoapObject) (response.getProperty(0))).getProperty(0).toString();
                // System.out.println("Possible Master Number: " + possibleMasterNumber);
                // if we don't have a master number yet
                if (!possibleMasterNumber.equals("anyType{}") && MASTER_NUMBER == null) {
                    MASTER_NUMBER = possibleMasterNumber;
                    // System.out.println("NEW MASTER ORDER NUMBER: " + MASTER_NUMBER);
                    // else if we already have a master number, de-link this order from it's current master number
                    // so we can use the one we already have an apply later with web service call
                } else if (!possibleMasterNumber.equals("anyType{}") && MASTER_NUMBER != null) {
                    System.out.println("Going to delete master number off order: " + this.enteredSOPNumber);
                    Thread thread = new Thread(() -> new DeleteMasterOrderDetails(possibleMasterNumber).execute());
                    thread.start();
                    thread.join();
                    // new DeleteMasterOrderDetails(possibleMasterNumber).execute();
                    // System.out.println("DELETED MASTER NUMBER OF ORDER: " + this.enteredSOPNumber);
                    // System.out.println("WE HAVE MASTER NUMBER: " + MASTER_NUMBER + " ALREADY");
                    // if we don't have a master number and neither does the order, get one
                } else {
                    // System.out.println("Getting new master number...");
                    Thread thread = new Thread(() -> new GetNextMasterOrderNumber().execute());
                    thread.start();
                    thread.join();
                    // new GetNextMasterOrderNumber().execute();
                    // System.out.println("NO MASTER NUMBER, NEW ONE FOUND: " + MASTER_NUMBER);
                }
                SOPNumber = ((SoapObject) (response.getProperty(0))).getProperty(1).toString();
                coolerLocation = ((SoapObject) (response.getProperty(0))).getProperty(2).toString();
                destination = ((SoapObject) (response.getProperty(0))).getProperty(3).toString();
                consignee = ((SoapObject) (response.getProperty(0))).getProperty(4).toString();
                truckStatus = ((SoapObject) (response.getProperty(0))).getProperty(5).toString();
                customerName = ((SoapObject) (response.getProperty(0))).getProperty(6).toString();
                isCheckedIn = ((SoapObject) (response.getProperty(0))).getProperty(7).toString();
                isAppointment = ((SoapObject) (response.getProperty(0))).getProperty(8).toString();
                orderDate = ((SoapObject) (response.getProperty(0))).getProperty(9).toString();
                appointmentTime = ((SoapObject) (response.getProperty(0))).getProperty(10).toString();
                estimatedWeight = ((SoapObject) (response.getProperty(0))).getProperty(11).toString();
                estimatedPallets = ((SoapObject) (response.getProperty(0))).getProperty(12).toString();
                MasterOrder masterOrder = new MasterOrder(MASTER_NUMBER, SOPNumber, coolerLocation, destination, consignee, truckStatus,
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
        if (propCount > -1) {
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
        } else if (propCount < 0){
            OrderEntry.validOrderNumber.setValue(0);
        }
        OrderEntry.possibleCustomerDestinations.clear();
    }
}
