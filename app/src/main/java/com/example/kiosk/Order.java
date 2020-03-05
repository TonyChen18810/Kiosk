package com.example.kiosk;

import java.util.ArrayList;

class Order {

    private String orderNumber;
    private String buyerName;
    private String destination;
    private String appointmentTime;

    // private static int orderCount;

    private static ArrayList<Order> orders = new ArrayList<>();

    Order(String orderNumber, String buyerName, String destination, String appointmentTime) {
        this.orderNumber = orderNumber;
        this.buyerName = buyerName;
        this.destination = destination;
        this.appointmentTime = appointmentTime;
    }

    String getOrderNumber() {
        return orderNumber;
    }

    String getBuyerName() {
        return buyerName;
    }

    String getAppointmentTime() {
        return appointmentTime;
    }

    public String getDestination() {
        return destination;
    }

    static ArrayList<Order> getOrders() {
        return orders;
    }

    static void addOrder(Order order) {
        orders.add(order);
    }

    static void removeOrder(int i) {
        orders.remove(i);
    }

    static int getSize() {
        return orders.size();
    }

    static void clearOrders() {
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
