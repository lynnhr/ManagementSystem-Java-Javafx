package com.example.managementsystem;

public class Product {
    private  int id;
    private String name;
    private double price;
    private String description;
    private int stockQuantity;
    private String category;
    private String imagePath;


    public Product(String name, double price, String description, int stockQuantity, String category, String imagePath) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.imagePath = imagePath;
    }
    public Product(int id, String name, double price, String description, int stockQuantity, String category, String imagePath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.imagePath = imagePath;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void updateStock(int quantity) {
        this.stockQuantity += quantity;
        if (this.stockQuantity < 0) {
            this.stockQuantity = 0;
        }
        System.out.println("Stock for " + this.name + " updated to " + this.stockQuantity);
    }
    public String displayDetails() {
        return String.format("Name: %s\nPrice: %.2f\nDescription: %s\nStock: %d\nCategory: %s\nImage Path: %s",
                name, price, description, stockQuantity, category, imagePath);
    }
    public void ManageProductDetails() {

    }
}
