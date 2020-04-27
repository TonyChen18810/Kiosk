package com.dbc.kiosk.Webservices;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import androidx.recyclerview.widget.RecyclerView;
import com.dbc.kiosk.Dialogs.ConnectedOrders;
import com.dbc.kiosk.Helpers.RecyclerViewHorizontalAdapter;
import com.dbc.kiosk.Helpers.Time;
import com.dbc.kiosk.Order;
import com.dbc.kiosk.R;
import com.dbc.kiosk.Screens.OrderEntry;
import com.dbc.kiosk.Settings;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.lang.ref.WeakReference;
import java.util.Date;
/**
 * GetOrderDetailsByMasterNumber.java
 *
 * @param String inMasterNumber, Activity activity
 *
 * Uses "GetOrderDetailsByMasterNumber" web service to retrieve all orders
 * connected to the provided master number
 *
 * Called from OrderEntry.java after the "Add Order" button is pressed.
 * Returns a list of any orders that are under the provided master number,
 * otherwise returns an empty list.
 *
 * Used to populate the RecyclerView in ConnectedOrders.java, shown as a
 * pop-up list after pressing "Add Order" in OrderEntry.java
 */
public class GetOrderDetailsByMasterNumber extends AsyncTask<Void, Void, Void> {

    private String inMasterNumber;
    private static int propertyCount;
    private WeakReference<Activity> mWeakActivity;

    private boolean connection;

    public GetOrderDetailsByMasterNumber(String inMasterNumber, Activity activity) {
        this.inMasterNumber = inMasterNumber;
        mWeakActivity = new WeakReference<>(activity);
        this.connection = false;
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
            if (response != null) {
                connection = true;
            }
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
                    System.out.println("Order date: " + orderDate);
                    System.out.println("Today's date: " + Time.getCurrentDate());
                    for (int j = 0; j < Order.getOrdersList().size(); j++) {
                        if (Order.getOrdersList().get(j).getSOPNumber().equals(SOPNumber) || isCheckedIn.equals("true") || !orderDate.equals(Time.getCurrentDate())) {
                            canBeInserted = false;
                        }
                    }

                    for (int j = 0; j < Order.getAssociatedOrdersList().size(); j++) {
                        if (Order.getAssociatedOrdersList().get(j).getSOPNumber().equals(SOPNumber) || isCheckedIn.equals("true") || !orderDate.equals(Time.getCurrentDate())) {
                            canBeInserted = false;
                        }
                    }

                    if (canBeInserted) {
                        Order order = new Order(masterNumber, SOPNumber, coolerLocation, destination, consignee, truckStatus,
                                customerName, isCheckedIn, isAppointment, orderDate, appointmentTime, estimatedWeight, estimatedPallets);
                        Order.addAssociatedOrderToList(order);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            connection = false;
            System.out.println("Trying again...");
            Thread thread = new Thread(() -> {
                new GetOrderDetailsByMasterNumber(inMasterNumber, mWeakActivity.get()).execute();
            });
            try {
                thread.start();
                Thread.sleep(3000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (connection) {
            if (propertyCount < 1 && Order.getAssociatedOrdersList().size() < 1) {
                System.out.println("No associated orders");
                if (Order.getCurrentOrder().getAppointment().equals("true") && GetOrderDetails.checkApppointmentTime(Order.getCurrentOrder().getAppointmentTime()) == -1) {
                    OrderEntry.appointmentTimeListener.setValue(-2);
                    OrderEntry.appointmentTimeListener.setValue(-100); // reset value for next check if there is another
                } else if (Order.getCurrentOrder().getAppointment().equals("true") && GetOrderDetails.checkApppointmentTime(Order.getCurrentOrder().getAppointmentTime()) == 1) {
                    OrderEntry.appointmentTimeListener.setValue(1);
                    OrderEntry.appointmentTimeListener.setValue(-100); // reset value for next check if there is another
                }
            } else if (Order.getAssociatedOrdersList().size() > 0){
                System.out.println("There's associated orders!");
                Activity activity = mWeakActivity.get();
                if (activity != null) {
                    RecyclerView recyclerView = activity.findViewById(R.id.OrdersView);
                    RecyclerViewHorizontalAdapter adapter = OrderEntry.getAdapter();
                    ConnectedOrders dialog = new ConnectedOrders(activity, recyclerView, adapter);
                    dialog.show();
                    dialog.setCancelable(false);
                }
            }
            Activity activity = mWeakActivity.get();
            if (activity != null) {
                activity.findViewById(R.id.progressBar).setVisibility(View.GONE);
                EditText orderNumber = activity.findViewById(R.id.OrderNumberBox);
                orderNumber.setEnabled(true);
                orderNumber.setFocusable(true);
                orderNumber.requestFocus();
            }
        }
    }
}
