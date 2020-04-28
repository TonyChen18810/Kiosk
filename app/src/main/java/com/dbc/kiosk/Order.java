package com.dbc.kiosk;

import com.dbc.kiosk.Helpers.Rounder;
import com.dbc.kiosk.Webservices.GetOrderDetails;
import java.util.ArrayList;
import java.util.List;

/**
 * Order.java
 *
 * Stores information for each order, retrieved from either GetOrderDetails.java
 * web service or GetOrderDetailsByMasterNumber.java.
 * 
 * ordersList stores all orders entered and all orders selected from associatedOrdersList by the driver
 *
 * associatedOrdersList stores all orders returned by GetOrderDetailsByMasterNumber that aren't checked-in.
 *
 * outlierOrders stores all orders that are in associatedOrdersList and eventually fall
 * under a different master number once the orders are updated in OrderSummary.java.
 * The orders that were not selected by the user will not be updated to the new master number
 * and need to be, so this list will be used to keep track of those orders.
 * 
 * totalWeight and totalPalletCount track the total added amount of all the added
 * orders. These are adjusted when addOrderToList() or removeOrderFromList() is called.
 *
 * reset() is used when FirstScreen.java is called (either from starting the app or from pressing "logout" button
 * clears all lists and values to prepare the app for a different user
 */
public class Order {

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

    private static Order CURRENT_ORDER;
    private static String CURRENT_APPOINTMENT_TIME;
    private static ArrayList<Order> ordersList = new ArrayList<>();
    private static ArrayList<Order> associatedOrdersList = new ArrayList<>();
    private static List<Order> outlierOrders = new ArrayList<>();
    private static double totalWeight = 0.0;
    private static double totalPalletCount = 0.0;

    /**
     * @param masterNumber Master number of order, may not have one - "anyType{}". If it does have one,
     *                     it most likely has connected orders (ConnectedOrders.java, RecyclerViewAssociatedAdapter.java)
     *
     * @param SOPNumber Order number, user types this in to enter an order
     * @param coolerLocation 01 or 06
     * @param destination (city, state)
     * @param consignee String name
     * @param truckStatus Outstanding, Fulfilled, Arrived. Order is not valid for check-in unless it is "Outstanding"
     * @param customerName String name
     * @param isCheckedIn "True" or "False". Order not valid for check-in unless this is "False"
     *
     * @param isAppointment "True" or "False". If this is "True" but appointmentTime is "00:00:00" - can't be checked-in
     *                      If this is "True" and appointmentTime is not "00:00:00" then this time must be within an hour
     *                      of Time.getCurrentTime() to be checked-in (on time).
     *
     * @param orderDate Date of the order. If it is not equal to today's date, invalid for check-in
     * @param appointmentTime Time of the appointment. If isAppointment is "False", this field doesn't matter
     * @param estimatedWeight Weight of the order
     * @param estimatedPallets Pallet count of the order
     *
     * CURRENT_ORDER = this; sets this order as the current order. The current order can be checked
     * using Order.getCurrentOrder()
     */
    public Order(String masterNumber, String SOPNumber, String coolerLocation, String destination, String consignee,
                 String truckStatus, String customerName, String isCheckedIn, String isAppointment,
                 String orderDate, String appointmentTime, String estimatedWeight, String estimatedPallets) {
        this.masterNumber = masterNumber;
        this.SOPNumber = SOPNumber;
        this.coolerLocation = coolerLocation;
        this.destination = destination;
        this.consignee = consignee;
        this.truckStatus = truckStatus;
        this.customerName = customerName;
        this.isCheckedIn = isCheckedIn;
        this.isAppointment = isAppointment;
        this.orderDate = orderDate;
        this.appointmentTime = appointmentTime;
        this.estimatedWeight = estimatedWeight;
        this.estimatedPallets = estimatedPallets;
        CURRENT_ORDER = this;
    }

    public String getMasterNumber() {
        return masterNumber;
    }

    public String getSOPNumber() {
        return SOPNumber;
    }

    public String getCoolerLocation() {
        return coolerLocation;
    }

    public String getDestination() {
        return destination;
    }

    public String getConsignee() {
        return consignee;
    }

    public String getTruckStatus() {
        return truckStatus;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCheckedIn() {
        return isCheckedIn;
    }

    public String getAppointment() {
        return isAppointment;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public static String getCurrentAppointmentTime() {
        return CURRENT_APPOINTMENT_TIME;
    }

    public static void setCurrentAppointmentTime(String appointmentTime) {
        CURRENT_APPOINTMENT_TIME = appointmentTime;
    }

    public static Order getOrderByOrderNumber(String SOPNumber) {
        for (Order order : ordersList) {
            if (order.SOPNumber.equals(SOPNumber)) {
                return order;
            }
        }
        return null;
    }

    public double getEstimatedWeight() {
        return Rounder.round(Double.parseDouble(estimatedWeight), 1);
    }

    public double getEstimatedPallets() {
        return Rounder.round(Double.parseDouble(estimatedPallets), 2);
    }

    public static double getTotalWeight() {
        return totalWeight;
    }

    public static double getTotalPalletCount() {
        return totalPalletCount;
    }

    // Used to reset all values in the case of a logout
    // or whenever the user finishes order check-ins
    // Called from FirstScreen.java
    public static void reset() {
        totalWeight = 0.0;
        totalPalletCount = 0.0;
        associatedOrdersList.clear();
        ordersList.clear();
        outlierOrders.clear();
        CURRENT_ORDER = null;
        GetOrderDetails.setNewMasterNumber(null);
    }

    public static void clearAssociatedOrderList() {
        associatedOrdersList.clear();
    }

    public static List<Order> getOrdersList() {
        return ordersList;
    }

    public static List<Order> getAssociatedOrdersList() {
        return associatedOrdersList;
    }

    public static List<Order> getOutlierOrders() {
        return outlierOrders;
    }

    public static Order getCurrentOrder() {
        return CURRENT_ORDER;
    }

    public static void addOrderToList(Order order) {
        totalWeight += order.getEstimatedWeight();
        totalPalletCount += order.getEstimatedPallets();
        ordersList.add(order);
    }


    public static void removeOrderFromList(int i) {
        totalWeight -= ordersList.get(i).getEstimatedWeight();
        totalPalletCount -= ordersList.get(i).getEstimatedPallets();
        ordersList.remove(i);
    }

    public static void addOrderToOutlierList(Order order) {
        outlierOrders.add(order);
    }

    public static void removeOrderFromOutlierList(Order order) {
        outlierOrders.remove(order);
    }

    public static void addAssociatedOrderToList(Order order) {
        associatedOrdersList.add(order);
    }
}
