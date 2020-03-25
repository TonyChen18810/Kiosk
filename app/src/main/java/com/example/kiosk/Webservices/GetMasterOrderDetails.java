package com.example.kiosk.Webservices;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import com.example.kiosk.Helpers.Time;
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

    private int propertyCount;

    private String masterNumber;
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
            propertyCount = response.getPropertyCount();
            System.out.println("PROPERTY COUNT ------- : " + propertyCount);
            if (propertyCount > 0) {
                masterNumber = ((SoapObject) (response.getProperty(0))).getProperty(0).toString();
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
        boolean isGoodOrder = false;

        if (propertyCount > 0) {// get rid of TRUE part after some tests
            if (isCheckedIn.equals("false")) { // || isCheckedIn.equals("true")) {
                if (isAppointment.equals("true") && appointmentTime.equals("00:00:00")) {
                    System.out.println("Need to make appointment");
                    OrderEntry.validOrderNumber.setValue(2);
                } else if (isAppointment.equals("true")) {
                    System.out.println("Has an appointment, now check for late/early/on-time");
                    if (checkApppointmentTime(appointmentTime) == -1) {
                        System.out.println("You're early");
                        if (MASTER_NUMBER == null) {
                            if (masterNumber.equals("anyType{}") || masterNumber.equals("")) {
                                System.out.println("We need a new master number...");
                                new GetNextMasterOrderNumber().execute();
                                isGoodOrder = true;
                            }
                        }
                        // OrderEntry.appointmentTimeListener.setValue(-1);
                    } else if (checkApppointmentTime(appointmentTime) == 1) {
                        System.out.println("You're late");
                        isGoodOrder = false;
                        OrderEntry.appointmentTimeListener.setValue(1);
                    } else if (checkApppointmentTime(appointmentTime) == 0){
                        System.out.println("On time");
                        if (MASTER_NUMBER == null) {
                            if (masterNumber.equals("anyType{}") || masterNumber.equals("")) {
                                System.out.println("We need a new master number...");
                                new GetNextMasterOrderNumber().execute();
                                isGoodOrder = true;
                            }
                        }
                        isGoodOrder = true;
                    }
                } else {
                    System.out.println("No appointment - continue");
                    if (MASTER_NUMBER == null) {
                        if (masterNumber.equals("anyType{}") || masterNumber.equals("")) {
                            System.out.println("We need a new master number...");
                            new GetNextMasterOrderNumber().execute();
                            isGoodOrder = true;
                        } else {
                            MASTER_NUMBER = masterNumber;
                            isGoodOrder = true;
                        }
                    } else {
                        isGoodOrder = true;
                    }
                    // MasterOrder.addMasterOrderToList(masterOrder);
                }
            }

            else if (isCheckedIn.equals("true")){
                System.out.println("Order already checked in");
                OrderEntry.validOrderNumber.setValue(3);
            }

        } else {
            // Order doesn't exist, invalid
            OrderEntry.validOrderNumber.setValue(0);
        }
        if (isGoodOrder) {
            MasterOrder masterOrder = new MasterOrder(MASTER_NUMBER, SOPNumber, coolerLocation, destination, consignee, truckStatus,
                    customerName, isCheckedIn, isAppointment, orderDate, appointmentTime, estimatedWeight, estimatedPallets);
            OrderEntry.validOrderNumber.setValue(1);
        }

        if (activity != null) {
            ProgressBar progressBar = activity.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
        }
    }

    public static int checkApppointmentTime(String appointmentTime) {
        int aptCode;
        System.out.println("Appointment time: " + appointmentTime);
        System.out.println("Logged in time: " + Time.getCurrentTime());
        char[] aptC = appointmentTime.toCharArray();
        char[] timeC = Time.getCurrentTime().toCharArray();
        String aptHour = ((aptC[0]-'0') + "" + (aptC[1]-'0'));
        System.out.println("Hour of apt: " + aptHour);
        String loggedInHour = ((timeC[0]-'0') + "" + (timeC[1]-'0'));
        System.out.println("Hour of logged in time: " + loggedInHour);

        int timeA = Integer.parseInt(aptHour);
        int timeB = Integer.parseInt(loggedInHour);
        System.out.println("timeA: " + timeA);
        System.out.println("timeB: " + timeB);

        if (timeB < timeA - 1) {
            System.out.println("EARLY!");
            aptCode = -1;
        } else if (timeB > timeA + 1) {
            System.out.println("LATE!");
            aptCode = 1;
        } else {
            System.out.println("ON TIME!");
            aptCode = 0;
        }
        return aptCode;
    }
}
