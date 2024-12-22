package com.example.managementsystem;
public class OrderItem {
    private int orderId;
    private int productId;
    private String productName;
    private double productPrice;

    public OrderItem(int orderId, int productId, String productName, double productPrice) {
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
    }

    // Getters and setters
    public int getOrderId() {
        return orderId;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getProductPrice() {
        return productPrice;
    }
}
