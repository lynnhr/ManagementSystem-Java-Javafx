package com.example.managementsystem;

public class InvoiceItem {
    private String productName;
    private double price;
    private int quantity;
    private double totalPrice;

    public InvoiceItem(String productName, double price, int quantity, double totalPrice) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public String getProductName() { return productName; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public double getTotalPrice() { return totalPrice; }
}

