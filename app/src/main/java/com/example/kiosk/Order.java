package com.example.kiosk;

import com.example.kiosk.Helpers.Rounder;
import com.example.kiosk.Webservices.GetOrderDetails;
import java.util.ArrayList;
import java.util.List;

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
    private static ArrayList<Order> possibleOrdersList = new ArrayList<>();
    private static ArrayList<Order> associatedOrdersList = new ArrayList<>();
    private static double totalWeight = 0.0;
    private static double totalPalletCount = 0.0;

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
        return Rounder.round(Double.parseDouble(estimatedPallets), 1);
    }

    public static double getTotalWeight() {
        return totalWeight;
    }

    public static double getTotalPalletCount() {
        return totalPalletCount;
    }

    public static void reset() {
        totalWeight = 0.0;
        totalPalletCount = 0.0;
        possibleOrdersList.clear();
        associatedOrdersList.clear();
        ordersList.clear();
        CURRENT_ORDER = null;
        GetOrderDetails.setNewMasterNumber(null);
    }

    public static void clearAssociatedOrderList() {
        associatedOrdersList.clear();
    }

    public static List<Order> getOrdersList() {
        return ordersList;
    }

    public static List<Order> getPossibleOrdersList() {
        return possibleOrdersList;
    }

    public static List<Order> getAssociatedOrdersList() {
        return associatedOrdersList;
    }

    public static Order getCurrentOrder() {
        return CURRENT_ORDER;
    }

    public static void addMasterOrderToList(Order order) {
        totalWeight += order.getEstimatedWeight();
        /*
        if (order.getEstimatedPallets() < 1 && order.getEstimatedPallets() > 0) {
            totalPalletCount += 1;
        } else {
            totalPalletCount += order.getEstimatedPallets();
        }
        */
        totalPalletCount += order.getEstimatedPallets();
        ordersList.add(order);
    }

    public static void removeMasterOrderFromList(int i) {
        totalWeight -= ordersList.get(i).getEstimatedWeight();
        totalPalletCount -= ordersList.get(i).getEstimatedPallets();
        ordersList.remove(i);
    }

    public static void addPossibleMasterOrderToList(Order order) {
        possibleOrdersList.add(order);
    }

    public static void addAssociatedMasterOrderToList(Order order) {
        associatedOrdersList.add(order);
    }
}
