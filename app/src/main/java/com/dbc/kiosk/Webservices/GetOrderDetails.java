package com.dbc.kiosk.Webservices;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import com.dbc.kiosk.Account;
import com.dbc.kiosk.Helpers.Time;
import com.dbc.kiosk.Order;
import com.dbc.kiosk.R;
import com.dbc.kiosk.Screens.OrderEntry;
import com.dbc.kiosk.Settings;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.lang.ref.WeakReference;
/**
 * GetOrderDetails.java
 *
 * @param Activity activity, String enteredSOPNumber
 *
 * Uses "GetMasterOrderDetails" web service to get information on provided SOP number (enteredSOPNumber)
 *
 * Called when the checkOrderBtn is pressed in OrderEntry.java (green arrow next to order number field)
 */
public class GetOrderDetails extends AsyncTask<Void, Void, Void> {

    private FirebaseAnalytics mFirebaseAnalytics;

    private WeakReference<Activity> mWeakActivity;
    private String enteredSOPNumber;

    private static String MASTER_NUMBER = null;

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

    private boolean connection = false;

    public GetOrderDetails(Activity activity, String enteredSOPNumber) {
        mWeakActivity = new WeakReference<>(activity);
        this.enteredSOPNumber = enteredSOPNumber;
        System.out.println("MASTER NUMBER ON METHOD CALL: " + MASTER_NUMBER);
    }

    public static void setNewMasterNumber(String newMasterNumber) {
        MASTER_NUMBER = newMasterNumber;
        System.out.println("NEW MASTER NUMBER: " + MASTER_NUMBER);
    }

    public static String getMasterNumber() {
        return MASTER_NUMBER;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String namespace = "http://tempuri.org/";
        String method = "GetMasterOrderDetails";
        String soapAction = "http://tempuri.org/GetMasterOrderDetails";
        // String URL = "http://vmiis/DBCWebService/DBCWebService.asmx";
        // String URL = "http://VMSQLTEST/DBCWebService/DBCWebService.asmx";
        String URL = Settings.getDbcUrl();

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("inSOPNumber", enteredSOPNumber);
        request.addProperty("inCoolerLocation", Settings.getCoolerLocation());
        System.out.println("Settings.getCoolerLocation(): " + Settings.getCoolerLocation());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transportSE = new HttpTransportSE(URL);

        try {
            transportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            propertyCount = response.getPropertyCount();
            if (response != null) {
                connection = true;
            }
            System.out.println("PROPERTY COUNT ------- : " + propertyCount);
            if (propertyCount > 0) {
                masterNumber = ((SoapObject) (response.getProperty(0))).getProperty(0).toString();
                System.out.println("Master number of this ORDER1: " + masterNumber);
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
                System.out.println("HERE'S THE APPOINTMENT TIME: " + appointmentTime);
                estimatedWeight = ((SoapObject) (response.getProperty(0))).getProperty(11).toString();
                estimatedPallets = ((SoapObject) (response.getProperty(0))).getProperty(12).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Internet connection issue...");
            connection = false;
            // if web service call fails, wait 3 seconds and try again - this continues until the call is successful
            Thread thread = new Thread(() -> new GetOrderDetails(mWeakActivity.get(), enteredSOPNumber).execute());
            try {
                Thread.sleep(3000);
                thread.start();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Activity activity = mWeakActivity.get();

        // checks if the order has any errors (late, unscheduled apt, checked in, etc.)
        // if order is good, it can be added - otherwise pop-up message is displayed
        boolean isGoodOrder = false;

        if (propertyCount < 1 && connection) {
            // order doesn't exist
            OrderEntry.validOrderNumber.setValue(0);
        }

        System.out.println("connection: " + connection);
        if (connection) {
            if (truckStatus == null || truckStatus.equals("null")) {
                isGoodOrder = false;
                System.out.println("Order doesn't exist, let's double check it's values: ");
                System.out.println("truckStatus: " + truckStatus);
                System.out.println("SOPnumber: " + SOPNumber);
                // OrderEntry.validOrderNumber.setValue(0);
            } else {
                System.out.println("truckStatus: " + truckStatus);
                System.out.println("Order date: " + orderDate);
                System.out.println("Today's date: " + Time.getCurrentDate());
                if (truckStatus.equals("Outstanding") && orderDate.equals(Time.getCurrentDate())) {
                    System.out.println("The dates are equal");
                    System.out.println("propertyCount: " + propertyCount);
                    if (propertyCount > 0) {
                        System.out.println("THIS IS FOR ORDER NUMBER: " + SOPNumber);
                        if (isCheckedIn.equals("false")) {
                            if (isAppointment.equals("true") && appointmentTime.equals("00:00:00")) {
                                System.out.println("Need to make appointment");
                                OrderEntry.validOrderNumber.setValue(2);
                            } else if (isAppointment.equals("true")) {
                                System.out.println("Has an appointment, now check for late/early/on-time");
                                if (checkApppointmentTime(appointmentTime) == -1) {
                                    System.out.println("You're early");
                                    isGoodOrder = true;
                                } else if (checkApppointmentTime(appointmentTime) == 1) {
                                    System.out.println("You're late");
                                    isGoodOrder = false;
                                    // OrderEntry.appointmentTimeListener.setValue(1);
                                    OrderEntry.validOrderNumber.setValue(6);
                                } else if (checkApppointmentTime(appointmentTime) == 0) {
                                    System.out.println("On time");
                                    isGoodOrder = true;
                                }
                            } else {
                                System.out.println("No appointment - continue");
                                if (MASTER_NUMBER == null) {
                                    if (masterNumber.equals("anyType{}") || masterNumber.equals("")) {
                                        isGoodOrder = true;
                                    } else {
                                        // MASTER_NUMBER = masterNumber;
                                        isGoodOrder = true;
                                    }
                                } else {
                                    isGoodOrder = true;
                                }
                            }
                        } else if (isCheckedIn.equals("true") || !truckStatus.equals("Outstanding")) {
                            isGoodOrder = false;
                            System.out.println("Order already checked in");
                            OrderEntry.validOrderNumber.setValue(3);
                        }
                    } else {
                        // Order doesn't exist, invalid
                        System.out.println("Order doesn't exist, let's double check it's values: ");
                        System.out.println("truckStatus: " + truckStatus);
                        System.out.println("SOPnumber: " + SOPNumber);
                        OrderEntry.validOrderNumber.setValue(0);
                        /*
                        mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);
                        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
                        mFirebaseAnalytics.setMinimumSessionDuration(500);
                        mFirebaseAnalytics.setSessionTimeoutDuration(300);
                        Bundle bundle = new Bundle();
                        bundle.putString("MASTER_NUMBER", GetOrderDetails.getMasterNumber());
                        bundle.putString("EMAIL", Account.getCurrentAccount().getEmail());
                        bundle.putString("PHONE", Account.getCurrentAccount().getPhoneNumber());
                        bundle.putString("TRUCK", Account.getCurrentAccount().getTruckName() + " " + Account.getCurrentAccount().getTruckNumber());
                        bundle.putString("TIME", Time.getCurrentTime());
                        for (int i = 0; i < Order.getOrdersList().size(); i++) {
                            String paramName = "ORDER" + i;
                            bundle.putString(paramName, Order.getOrdersList().get(i).getSOPNumber());
                        }
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, bundle);
                         */
                    }
                } else if (!truckStatus.equals("Outstanding")) {
                    System.out.println("Order already checked in / not Outstanding");
                    OrderEntry.validOrderNumber.setValue(3);
                } else if (!orderDate.equals(Time.getCurrentDate())) {
                    System.out.println("Dates do not match");
                    OrderEntry.validOrderNumber.setValue(4);
                }
            }
            if (isGoodOrder) {
                System.out.println("MASTER_NUMBER: " + MASTER_NUMBER);
                System.out.println("masterNumber: " + masterNumber);
                // sets current order
                Order order = new Order(masterNumber, SOPNumber, coolerLocation, destination, consignee, truckStatus,
                        customerName, isCheckedIn, isAppointment, orderDate, appointmentTime, estimatedWeight, estimatedPallets);
                // OrderEntry.validOrderNumber.setValue(1); // this initializes the step to let people add the order
                // to their list of orders - here is where the customer pop-up is called
                // instead, now check if there are connected orders (master #?) and IF any of those connected orders are checked-in,
                // make sure they were checked-in by this driver. If they weren't checked in by this driver, invalid order #.
                // If none are checked-in -> valid order #.

                // Is there a master number? if so, most likely connected orders.
                if (masterNumber != null && !masterNumber.equals("anyType{}") && !masterNumber.equals("")) {
                    new CheckConnectedOrders(Account.getCurrentAccount().getEmail(), masterNumber, true).execute();
                } else {
                    if (activity != null) {
                        ProgressBar progressBar = activity.findViewById(R.id.progressBar);
                        progressBar.setVisibility(View.GONE);
                    }
                    OrderEntry.validOrderNumber.setValue(1);
                }
            }
        }
    }

    public static int checkApppointmentTime(String appointmentTime) {
        int aptCode = 0;
        // System.out.println("Appointment time: " + appointmentTime);
        // System.out.println("Logged in time: " + Time.getCurrentTime());
        char[] aptC = appointmentTime.toCharArray();
        char[] timeC = Time.getCurrentTime().toCharArray();
        String aptHour = ((aptC[0]-'0') + "" + (aptC[1]-'0'));
        // System.out.println("Hour of apt: " + aptHour);
        String loggedInHour = ((timeC[0]-'0') + "" + (timeC[1]-'0'));
        // System.out.println("Hour of logged in time: " + loggedInHour);
        String aptMinute = ((aptC[3]-'0') + "" + (aptC[4]-'0'));
        // System.out.println("Minutes of apt: " + aptMinute);
        String loggedInMinute = ((timeC[3]-'0') + "" + (timeC[4]-'0'));
        // System.out.println("Minutes of logged in time: " + loggedInMinute);

        int aptHourInt = Integer.parseInt(aptHour);
        int aptMinuteInt = Integer.parseInt(aptMinute);
        int loggedHourInt = Integer.parseInt(loggedInHour);
        int loggedMinuteInt = Integer.parseInt(loggedInMinute);
        System.out.println("Apt. hour: " + aptHourInt);
        System.out.println("Apt. minute: " + aptMinuteInt);
        System.out.println("Logged in hour: " + loggedHourInt);
        System.out.println("Logged in minute: " + loggedMinuteInt);

        // aptCode -1 = early
        // aptCode 1 = late
        if (aptHourInt - 1 > loggedHourInt) {
            aptCode = -1;
            System.out.println("early");
        } else if (aptHourInt - 1 == loggedHourInt) {
            if (aptMinuteInt > loggedMinuteInt) {
                aptCode = -1;
                System.out.println("early");
            }
        } else if (aptHourInt + 1 < loggedHourInt) {
            aptCode = 1;
            System.out.println("late");
        } else if (aptHourInt + 1 <= loggedHourInt) {
            if (aptMinuteInt < loggedMinuteInt) {
                aptCode = 1;
                System.out.println("late");
            }
        }
        if (aptCode == 0) {
            System.out.println("on-time");
        }
        return aptCode;
    }
}
