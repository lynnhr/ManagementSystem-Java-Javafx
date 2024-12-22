package com.example.managementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CartCRUD {
    private Connection connection;

    public CartCRUD() throws SQLException {
        connection = Database.getInstance().getConnection();
    }

    public ResultSet getCart() {
        try {
            User user = User.getUser();
            int userId = user.getUserID();

            String query = "SELECT c.cart_id, p.name, p.price, c.quantity, (p.price * c.quantity) AS total_price " +
                    "FROM cart c " +
                    "JOIN products p ON c.product_id = p.product_id " +
                    "WHERE c.user_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, userId);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addToCart(int productId, int quantity) {
        try {
            User user = User.getUser();
            int userId = user.getUserID();

            String query = "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE quantity = quantity + ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            stmt.setInt(3, quantity);
            stmt.setInt(4, quantity);
            stmt.executeUpdate();
            System.out.println("Product added to cart for user ID: " + userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void removeFromCart(String productName) {
        try {
            User user = User.getUser();
            int userId = user.getUserID();

            String query = "DELETE c " +
                    "FROM cart c " +
                    "JOIN products p ON c.product_id = p.product_id " +
                    "WHERE c.user_id = ? AND p.name = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setString(2, productName);
            stmt.executeUpdate();
            System.out.println("Removed product: " + productName + " from the cart.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

