package com.example.kiosk.Webservices;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.example.kiosk.PossibleDestination;
import com.example.kiosk.R;
import com.example.kiosk.Screens.OrderEntry;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class GetPossibleShipTos extends AsyncTask<Void, Void, Void> {

    private WeakReference<Activity> mWeakActivity;
    private String enteredSOPNumber;
    private String coolerNumber = "01";
    private static List<PossibleDestination> possibleDestinations;
    private int propCount = 0;

    static List<PossibleDestination> getPossibleDestinations() {
        return possibleDestinations;
    }

    public GetPossibleShipTos(Activity activity, String enteredSOPNumber) {
        mWeakActivity = new WeakReference<>(activity);
        this.enteredSOPNumber = enteredSOPNumber;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        propCount = 0;
        String namespace = "http://tempuri.org/";
        String method = "GetPossibleShipTos";
        String soapAction = "http://tempuri.org/GetPossibleShipTos";
        // String URL = "http://vmiis/DBCWebService/DBCWebService.asmx";
        String URL = "http://VMSQLTEST/DBCWebService/DBCWebService.asmx";

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
            possibleDestinations = new ArrayList<>();
            if (response.getPropertyCount() > 0) {
                for (int i = 0; i < response.getPropertyCount(); i++) {
                    String SOPNumber = ((SoapObject) (response.getProperty(i))).getProperty(0).toString();
                    String coolerLocation = ((SoapObject) (response.getProperty(i))).getProperty(1).toString();
                    String destination = ((SoapObject) (response.getProperty(i))).getProperty(2).toString();
                    String matchesOrder = ((SoapObject) (response.getProperty(i))).getProperty(3).toString();
                    possibleDestinations.add(new PossibleDestination(SOPNumber, coolerLocation, destination, matchesOrder));
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
        Activity activity = mWeakActivity.get();
        if (activity != null) {
            // System.out.println("Possible destinations size: " + possibleDestinations.size());
            // System.out.println("Possible customer destinations size: " + OrderEntry.possibleCustomerDestinations.size());
            OrderEntry.possibleCustomerDestinations.clear();
            for (int i = 0; i < possibleDestinations.size(); i++) {
                System.out.println(getPossibleDestinations().get(i).getDestination());
                OrderEntry.possibleCustomerDestinations.add(possibleDestinations.get(i).getDestination());
            }
            activity.findViewById(R.id.SelectDestinationBtn).setEnabled(true);
            activity.findViewById(R.id.progressBar).setVisibility(View.GONE);
            // Button destinationBtn = activity.findViewById(R.id.SelectDestinationBtn).startAnimation(AnimationUtils.loadAnimation(mWeakActivity.get(), R.anim.fade));
        }
        if (propCount > 0) {
            OrderEntry.listListener.setValue(true);
        } else {
            propCount = 0;
            OrderEntry.listListener.setValue(false);
        }
    }
}
