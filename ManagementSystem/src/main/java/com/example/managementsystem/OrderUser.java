package com.example.managementsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderUser {

    public void showOrderForm() {
        Stage stage = new Stage();
        stage.setTitle("Place an Order");
        BorderPane root = new BorderPane();
        ImageView side = new ImageView("side.png");
        side.setFitHeight(750);
        side.setFitWidth(400);
        VBox vbox = new VBox(20);
        vbox.setLayoutX(100);
        vbox.setLayoutY(300);
        vbox.setPrefSize(350, 200);
        String[] textItems = {"Start Shopping", "View Cart", "Order History", "Settings"};
        for (String item : textItems) {
            Text text = new Text(item);
            text.setFill(javafx.scene.paint.Color.WHITE);
            text.setFont(Font.font("Calibri Bold", 20));

            // text.setEffect(new Glow());
            text.getStyleClass().add("MenuText");
            vbox.getChildren().add(text);

            text.setOnMouseClicked(e -> {
                stage.close();
                switch(text.getText()){
                    case "Start Shopping":
                        try {
                            DisplayProducts displayPr=new DisplayProducts();
                            displayPr.showProducts();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }

                        break;
                    case "View Cart":
                        DisplayCart displayCart = new DisplayCart();
                        try {
                            displayCart.showCart();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        break;
                    case "Order History":
                        OrderHistory orderHistory=new OrderHistory();
                        break;
                    case"Settings":
                        Settings settings=new Settings();
                        break;


                }

            });

        }
        AnchorPane pane=new AnchorPane(side,vbox);
        root.setLeft(pane);

        VBox form = new VBox(25);
        form.setPadding(new Insets(25));


// Street input field and error message
        VBox streetBox = new VBox(10);
        Label streetLabel = new Label("Street:");
        TextField streetField = new TextField();
        Label streetErrorLabel = new Label();  // Label for error message
        streetErrorLabel.setStyle("-fx-text-fill: red;");
        streetBox.getChildren().addAll(streetLabel, streetField, streetErrorLabel);
        form.getChildren().add(streetBox);


// Postal Code input field and error message
        VBox postalCodeBox = new VBox(10);
        Label postalCodeLabel = new Label("Postal Code:");
        TextField postalCodeField = new TextField();
        Label postalCodeErrorLabel = new Label();  // Label for error message
        postalCodeErrorLabel.setStyle("-fx-text-fill: red;");
        postalCodeBox.getChildren().addAll(postalCodeLabel, postalCodeField, postalCodeErrorLabel);
        form.getChildren().add(postalCodeBox);


// City input field and error message
        VBox cityBox = new VBox(10);
        Label cityLabel = new Label("City:");
        TextField cityField = new TextField();
        Label cityErrorLabel = new Label();  // Label for error message
        cityErrorLabel.setStyle("-fx-text-fill: red;");
        cityBox.getChildren().addAll(cityLabel, cityField, cityErrorLabel);
        form.getChildren().add(cityBox);


// Payment method ComboBox and error message
        VBox paymentBox = new VBox(10);
        Label paymentLabel = new Label("Payment Method:");
        ComboBox<String> paymentMethodComboBox = new ComboBox<>();
        paymentMethodComboBox.setPromptText("Select Payment Method");
        populatePaymentMethods(paymentMethodComboBox);
        Label paymentErrorLabel = new Label();  // Label for error message
        paymentErrorLabel.setStyle("-fx-text-fill: red;");
        paymentBox.getChildren().addAll(paymentLabel, paymentMethodComboBox, paymentErrorLabel);
        form.getChildren().add(paymentBox);


// Place Order button
        Button placeOrderButton = new Button("Next");
        placeOrderButton.setAlignment(Pos.BOTTOM_LEFT);
        form.getChildren().add(placeOrderButton);
        form.setAlignment(Pos.CENTER);



        placeOrderButton.setOnAction(e -> {

            String street = streetField.getText();
            String postalCode = postalCodeField.getText();
            String city = cityField.getText();
            String paymentMethod = paymentMethodComboBox.getValue();
            if (!validateForm(streetField, postalCodeField, cityField, paymentMethodComboBox, streetErrorLabel, postalCodeErrorLabel, cityErrorLabel, paymentErrorLabel)) {
                return;
            }


            if (street.isEmpty() || postalCode.isEmpty() || city.isEmpty() || paymentMethod == null) {
                showAlert("Please fill in all fields.");
            }
            else {



                    try {
                        int addressId = insertAddress(street, postalCode, city);
                        int paymentId = getPaymentId(paymentMethod);
                        int orderId = placeOrder(addressId, paymentId);

                        InvoiceGenerator invoiceGenerator = new InvoiceGenerator();
                        invoiceGenerator.showInvoice(orderId);

                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Order Confirmation");
                        successAlert.setHeaderText(null);
                        successAlert.setContentText("Order placed successfully!");
                        successAlert.showAndWait();
                        stage.close();


                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        showAlert("Error while placing the order: " + ex.getMessage());
                    }
                }


        });


        root.setCenter(form);

        Scene scene = new Scene(root, 1400, 750);
        ThemeManager.registerStage(stage);
        ThemeManager.applyThemeToScene(scene);

        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void populatePaymentMethods(ComboBox<String> paymentMethodComboBox) {
        ObservableList<String> paymentMethods = FXCollections.observableArrayList();
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT method FROM payment")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                paymentMethods.add(rs.getString("method"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        paymentMethodComboBox.setItems(paymentMethods);
    }

    private int insertAddress(String street, String postalCode, String city) throws SQLException {
        try (Connection connection = Database.getInstance().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO address (user_id, street, city, postal_code) VALUES (?, ?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            int userId = User.getUser().getUserID();
            stmt.setInt(1, userId);
            stmt.setString(2, street);
            stmt.setString(3, city);
            stmt.setString(4, postalCode);
            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            throw new SQLException("Failed to insert address.");
        }
    }

    private int getPaymentId(String method) throws SQLException {
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT payment_id FROM payment WHERE method = ?")) {
            stmt.setString(1, method);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("payment_id");
            } else {
                throw new SQLException("Payment method not found.");
            }
        }
    }



    public int placeOrder(int addressId, int paymentId) throws SQLException {
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmtSelect = connection.prepareStatement(
                     "SELECT c.product_id, c.quantity FROM cart c " +
                             "JOIN products p ON c.product_id = p.product_id " +
                             "WHERE c.user_id = ?")) {

            int userId = User.getUser().getUserID();
            System.out.println("User ID: " + userId); // Debugging line to check the user ID
            stmtSelect.setInt(1, userId);

            ResultSet rs = stmtSelect.executeQuery();

            // Start a transaction to ensure data integrity
            connection.setAutoCommit(false);
            try {
                // Create a new order
                PreparedStatement stmtOrder = connection.prepareStatement(
                        "INSERT INTO orders (user_id, address_id, payment_id, shipping_status) VALUES (?, ?, ?, ?)",
                        PreparedStatement.RETURN_GENERATED_KEYS);
                stmtOrder.setInt(1, userId);
                stmtOrder.setInt(2, addressId);
                stmtOrder.setInt(3, paymentId);
                stmtOrder.setString(4, "Pending");
                int rowsInserted = stmtOrder.executeUpdate();

                if (rowsInserted > 0) {
                    ResultSet generatedKeys = stmtOrder.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int orderId = generatedKeys.getInt(1);
                        System.out.println("Order created successfully with order ID: " + orderId);

                        // Insert items into the order_items table
                        while (rs.next()) {
                            int productId = rs.getInt("product_id");
                            int quantity = rs.getInt("quantity");
                            System.out.println("Inserting product ID: " + productId + ", Quantity: " + quantity);

                            PreparedStatement stmtInsert = connection.prepareStatement(
                                    "INSERT INTO order_items (order_id, product_id) VALUES (?, ?)");
                            stmtInsert.setInt(1, orderId);
                            stmtInsert.setInt(2, productId);
                            stmtInsert.executeUpdate();
                        }
                        System.out.println("All items added to order items table.");
                        connection.commit(); // Commit transaction
                        System.out.println("Order placed and transaction committed successfully.");

                        return orderId;  // Return the orderId after successful order placement
                    }
                }
                connection.commit(); // Commit transaction if everything goes well
            } catch (SQLException e) {
                connection.rollback(); // Rollback on failure
                System.err.println("Transaction rolled back due to an error: " + e.getMessage());
                throw new RuntimeException("Error while placing order", e);
            } finally {
                connection.setAutoCommit(true); // Reset auto-commit to true
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print the stack trace for debugging
            throw new RuntimeException("Error while placing order", e);
        }
        return -1;
    }
    private void highlightField(Control field, Label errorLabel, String errorMessage) {
        field.setStyle("-fx-border-color: #7a1616;");
        errorLabel.setText(errorMessage);
        errorLabel.setVisible(true);
    }
    private boolean validateForm(TextField streetField, TextField postalCodeField, TextField cityField, ComboBox<String> paymentMethodComboBox, Label streetErrorLabel, Label postalCodeErrorLabel, Label cityErrorLabel, Label paymentErrorLabel) {
        boolean isValid = true;

        // Validate postal code - must be numeric
        if (postalCodeField.getText().trim().isEmpty() || !postalCodeField.getText().matches("\\d+")) {
            highlightField(postalCodeField, postalCodeErrorLabel, "Postal Code must be numeric!");
            isValid = false;
        } else {
            resetField(postalCodeField, postalCodeErrorLabel);
        }

        // Validate street - must contain only letters (no numbers or special characters)
        if (streetField.getText().trim().isEmpty() || !streetField.getText().matches("[a-zA-Z\\s]+")) {
            highlightField(streetField, streetErrorLabel, "Street must only contain letters!");
            isValid = false;
        } else {
            resetField(streetField, streetErrorLabel);
        }

        // Validate city - must contain only letters (no numbers or special characters)
        if (cityField.getText().trim().isEmpty() || !cityField.getText().matches("[a-zA-Z\\s]+")) {
            highlightField(cityField, cityErrorLabel, "City must only contain letters!");
            isValid = false;
        } else {
            resetField(cityField, cityErrorLabel);
        }

        // Validate payment method - must be selected
        if (paymentMethodComboBox.getValue() == null || paymentMethodComboBox.getValue().trim().isEmpty()) {
            highlightField(paymentMethodComboBox, paymentErrorLabel, "Please select a payment method!");
            isValid = false;
        } else {
            resetField(paymentMethodComboBox, paymentErrorLabel);
        }

        return isValid;
    }



    private void resetField(Control field, Label errorLabel) {
        field.setStyle("-fx-border-color: none;");
        errorLabel.setVisible(false);
    }

    private void addInputRestrictions(TextField streetField, TextField postalCodeField, TextField cityField) {
        // Restrict Street to only letters and spaces
        streetField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[A-Za-z ]*")) {
                streetField.setText(oldValue);
            }
        });

        // Restrict Postal Code to only numbers
        postalCodeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                postalCodeField.setText(oldValue);
            }
        });

        // Restrict City to only letters and spaces
        cityField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[A-Za-z ]*")) {
                cityField.setText(oldValue);
            }
        });
    }

}
