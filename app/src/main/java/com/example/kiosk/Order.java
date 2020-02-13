package com.example.kiosk;

import java.util.ArrayList;

class Order {

    private String orderNumber;
    private String buyerName;
    private String destination;

    private static ArrayList<Order> orders = new ArrayList<>();

    public Order(String orderNumber, String buyerName, String destination) {
        this.orderNumber = orderNumber;
        this.buyerName = buyerName;
        this.destination = destination;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public static ArrayList<Order> getOrders() {
        return orders;
    }

    public static void addOrder(Order order) {
        orders.add(order);
    }

    public static int getSize() {
        return orders.size();
    }

    public static void clearOrders() {
        orders.clear();
    }
}
