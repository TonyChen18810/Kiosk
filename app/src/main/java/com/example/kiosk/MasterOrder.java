package com.example.kiosk;

import com.example.kiosk.Webservices.GetMasterOrderDetails;

import java.util.ArrayList;
import java.util.List;

public class MasterOrder {

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

    private static MasterOrder CURRENT_MASTER_ORDER;
    private static ArrayList<MasterOrder> masterOrdersList = new ArrayList<>();
    private static ArrayList<MasterOrder> possibleMasterOrdersList = new ArrayList<>();
    private static ArrayList<MasterOrder> associatedMasterOrdersList = new ArrayList<>();
    private static double totalWeight = 0;
    private static double totalPalletCount = 0;

    public MasterOrder(String masterNumber, String SOPNumber, String coolerLocation, String destination, String consignee,
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
        CURRENT_MASTER_ORDER = this;
    }

    public String getMasterNumber() {
        return masterNumber;
    }

    public void setMasterNumber(String masterNumber) {
        this.masterNumber = masterNumber;
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

    public double getEstimatedWeight() {
        return Double.parseDouble(estimatedWeight);
    }

    public double getEstimatedPallets() {
        return Double.parseDouble(estimatedPallets);
    }

    public static double getTotalWeight() {
        return totalWeight;
    }

    public static double getTotalPalletCount() {
        return totalPalletCount;
    }

    public static void reset() {
        totalWeight = 0;
        totalPalletCount = 0;
        possibleMasterOrdersList.clear();
        associatedMasterOrdersList.clear();
        masterOrdersList.clear();
        GetMasterOrderDetails.setNewMasterNumber(null);
    }

    public static List<MasterOrder> getMasterOrdersList() {
        return masterOrdersList;
    }

    public static List<MasterOrder> getPossibleMasterOrdersList() {
        return possibleMasterOrdersList;
    }

    public static List<MasterOrder> getAssociatedMasterOrdersList() {
        return associatedMasterOrdersList;
    }

    public static MasterOrder getCurrentMasterOrder() {
        return CURRENT_MASTER_ORDER;
    }

    public static void addMasterOrderToList(MasterOrder masterOrder) {
        totalWeight += masterOrder.getEstimatedWeight();
        totalPalletCount += masterOrder.getEstimatedPallets();
        masterOrdersList.add(masterOrder);
    }

    public static void removeMasterOrderFromList(int i) {
        totalWeight -= masterOrdersList.get(i).getEstimatedWeight();
        totalPalletCount -= masterOrdersList.get(i).getEstimatedPallets();
        masterOrdersList.remove(i);
    }

    public static void addPossibleMasterOrderToList(MasterOrder masterOrder) {
        possibleMasterOrdersList.add(masterOrder);
    }

    public static void addAssociatedMasterOrderToList(MasterOrder masterOrder) {
        associatedMasterOrdersList.add(masterOrder);
    }
}
