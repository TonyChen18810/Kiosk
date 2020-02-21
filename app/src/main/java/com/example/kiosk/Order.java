package com.example.kiosk;

import java.util.ArrayList;

class Order {

    private String orderNumber;
    private String buyerName;
    private String destination;

    private static ArrayList<Order> orders = new ArrayList<>();

    Order(String orderNumber, String buyerName, String destination) {
        this.orderNumber = orderNumber;
        this.buyerName = buyerName;
        this.destination = destination;
    }

    String getOrderNumber() {
        return orderNumber;
    }

    String getBuyerName() {
        return buyerName;
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

    static int getSize() {
        return orders.size();
    }

    static void clearOrders() {
        orders.clear();
    }
}
