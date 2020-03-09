package com.example.kiosk;

import java.util.ArrayList;

public class Order {

    private String orderNumber;
    private String buyerName;
    private String destination;
    private String appointmentTime;
    private int weight;
    private int palletCount;

    private static ArrayList<Order> orders = new ArrayList<>();
    private static int totalWeight = 0;
    private static int totalPalletCount = 0;

    public Order(String orderNumber, String buyerName, String destination, String appointmentTime, int weight, int palletCount) {
        this.orderNumber = orderNumber;
        this.buyerName = buyerName;
        this.destination = destination;
        this.appointmentTime = appointmentTime;
        this.weight = weight;
        this.palletCount = palletCount;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public String getDestination() {
        return destination;
    }

    public int getWeight() {
       return weight;
    }

    public int getPalletCount() {
        return palletCount;
    }

    static public int getTotalWeight() {
        return totalWeight;
    }

    static public int getTotalPalletCount() {
        return totalPalletCount;
    }

    public static ArrayList<Order> getOrders() {
        return orders;
    }

    public static void addOrder(Order order) {
        totalWeight += order.getWeight();
        totalPalletCount += order.getPalletCount();
        orders.add(order);
        System.out.println("Added order number: " + order.getOrderNumber());
        System.out.println("Added order pallet count: " + order.getPalletCount());
        System.out.println("Added order weight: " + order.getWeight());
        System.out.println("Total pallet count: " + getTotalPalletCount());
        System.out.println("Total weight: " + getTotalWeight());
    }

    public static void removeOrder(int i) {
        totalPalletCount -= orders.get(i).getPalletCount();
        totalWeight -= orders.get(i).getWeight();
        orders.remove(i);
    }

    public static int getSize() {
        return orders.size();
    }

    public static void clearOrders() {
        orders.clear();
    }

    public static void resetTotals() {
        totalWeight = 0;
        totalPalletCount = 0;
    }
}
