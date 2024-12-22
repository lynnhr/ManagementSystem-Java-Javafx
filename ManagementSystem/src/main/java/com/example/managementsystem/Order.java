package com.example.managementsystem;

public class Order {
    private int orderId;
    private int userId;
    private Payment payment; // Association with Payment class
    private Address address; // Association with Address class
    private String shippingStatus;

    // Constructor
    public Order(int userId, Address address, Payment payment, String shippingStatus) {
        this.userId = userId;
        this.address = address;
        this.payment = payment;
        this.shippingStatus = shippingStatus;
    }
    public Order(int orderId, int userId, Address address, Payment payment, String shippingStatus) {
        this.orderId=orderId;
        this.userId = userId;
        this.address = address;
        this.payment = payment;
        this.shippingStatus = shippingStatus;
    }
    public Order(int orderId, int userId, String shippingStatus) {
        this.orderId = orderId;
        this.userId = userId;
        this.shippingStatus = shippingStatus;
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getAddress() {
        return address.getCity()+" "+address.getStreet()+" "+address.getPostalCode();
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getShippingStatus() {
        return shippingStatus;
    }

    public void setShippingStatus(String shippingStatus) {
        this.shippingStatus = shippingStatus;
    }



}
