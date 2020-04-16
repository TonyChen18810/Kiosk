package com.example.kiosk.Webservices;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Button;
import com.example.kiosk.R;
import com.example.kiosk.Screens.OrderSummary;
import com.example.kiosk.Settings;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.lang.ref.WeakReference;
import java.util.Date;

/**
 * DriverNotification.java
 *
 * @param String inMasterNumber
 *
 * Uses "UpdateMasterOrderNotification" web service to send the driver
 * a notification via email and/or text message regarding their submitted orders
 *
 * Called from UpdateMasterOrder.java when lastCall = true (when the program has
 * finished submitting the orders)
 */
public class DriverNotification extends AsyncTask<Void, Void, Void> {

    private String inMasterNumber;
    private String inCoolerLocation = "01";

    private WeakReference<Activity> mWeakReference;

    DriverNotification(String inMasterNumber, Activity activity) {
        this.inMasterNumber = inMasterNumber;
        mWeakReference = new WeakReference<>(activity);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "UpdateMasterOrderNotification";
        String soapAction = "http://tempuri.org/UpdateMasterOrderNotification";
        // String URL = "http://vmiis/DBCWebService/DBCWebService.asmx";
        String URL = "http://VMSQLTEST/DBCWebService/DBCWebService.asmx";

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("inMasterNumber", inMasterNumber);
        request.addProperty("inCoolerLocation", inCoolerLocation);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transportSE = new HttpTransportSE(URL);

        try {
            transportSE.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            if (response.toString().equals("0")) {
                System.out.println("Notification successfully sent to driver");
            } else {
                System.out.println("Fail, notification not sent to driver.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Settings.setError(e.toString(), getClass().toString(), new Date().toString(), null);
            Thread thread = new Thread(() -> {
                new DriverNotification(inMasterNumber, mWeakReference.get()).execute();
            });
            thread.start();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                // Settings.setError(ex.toString(), getClass().toString(), new Date().toString(), null);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Activity activity = mWeakReference.get();
        if (activity != null) {
            Button logoutBtn = activity.findViewById(R.id.LogoutBtn);
            logoutBtn.setEnabled(true);
            OrderSummary.dialog.dismiss();;
            OrderSummary.timer.start();
        }
    }
}
