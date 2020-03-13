package com.example.kiosk.Webservices;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

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
    private static String masterNumber;
    private static String coolerNumber = "01";
    private static int propCount = 0;

    public GetMasterOrderDetails(Activity activity, String enteredSOPNumber) {
        mWeakActivity = new WeakReference<>(activity);
        this.enteredSOPNumber = enteredSOPNumber;
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

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transportSE = new HttpTransportSE(URL);

        try {
            transportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            propCount = response.getPropertyCount();
            if (response.getPropertyCount() > 0) {
                if (((SoapObject) (response.getProperty(0))).getProperty(0).toString().length() > 0) {
                    masterNumber = ((SoapObject) (response.getProperty(0))).getProperty(0).toString();
                } else {
                    Thread thread = new Thread(() -> new GetNextMasterOrderNumber().execute());
                    thread.start();
                }
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
                MasterOrder.addPossibleMasterOrderToList(masterOrder);
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
        if (propCount > 0) {
            OrderEntry.validOrderNumber.setValue(true);
        } else {
            OrderEntry.validOrderNumber.setValue(false);
        }
    }
}
