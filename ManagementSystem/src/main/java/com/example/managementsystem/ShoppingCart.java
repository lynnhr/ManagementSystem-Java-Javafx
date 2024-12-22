package com.example.managementsystem;
public class ShoppingCart {
    private String name;
    private double price;
    private int quantity;
    private double totalPrice;

    public ShoppingCart(String name, double price, int quantity, double totalPrice) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}
