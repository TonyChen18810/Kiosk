package com.example.kiosk;

import java.util.ArrayList;

public class Order {

    private String orderNumber;
    private String buyerName;
    private String destination;
    private String appointmentTime;

    // private static int orderCount;

    private static ArrayList<Order> orders = new ArrayList<>();

    public Order(String orderNumber, String buyerName, String destination, String appointmentTime) {
        this.orderNumber = orderNumber;
        this.buyerName = buyerName;
        this.destination = destination;
        this.appointmentTime = appointmentTime;
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

    public static ArrayList<Order> getOrders() {
        return orders;
    }

    public static void addOrder(Order order) {
        orders.add(order);
    }

    public static void removeOrder(int i) {
        orders.remove(i);
    }

    public static int getSize() {
        return orders.size();
    }

    public static void clearOrders() {
        orders.clear();
    }
/**
    static void incrementOrderCount() {
        orderCount++;
    }

    static void decrementOrderCount() {
        orderCount--;
    }

    static int getOrderCount() {
        return orderCount;
    }
 */
}
