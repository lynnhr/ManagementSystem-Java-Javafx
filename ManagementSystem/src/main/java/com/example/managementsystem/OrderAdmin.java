package com.example.managementsystem;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class OrderAdmin {

    private TableView<Order> ordersTable;
    private ObservableList<Order> ordersData;
    private TableView<OrderItem> orderItemsTable;
    private ObservableList<OrderItem> orderItemsData;

    public void showOrders() {
        Stage stage = new Stage();
        stage.setTitle("Admin: Manage Orders");
        BorderPane root = new BorderPane();
        ImageView side = new ImageView("side.png");
        side.setFitHeight(750);
        side.setFitWidth(400);
        VBox vbox = new VBox(20);
        vbox.setLayoutX(100);
        vbox.setLayoutY(300);
        vbox.setPrefSize(350, 200);
        String[] textItems = {"Products", "Orders", "Payment Method"};
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
                    case "Products":
                        ProductsManagement productsManagement=new ProductsManagement();
                        break;
                    case "Orders":
                        OrderAdmin orderAdmin=new OrderAdmin();
                        orderAdmin.showOrders();
                        break;
                    case "Payment Method":
                        AdminPaymentMethod adminPaymentMethod=new AdminPaymentMethod();
                        break;
                }

            });

        }
        AnchorPane pane=new AnchorPane(side,vbox);
        root.setLeft(pane);


        ordersTable = new TableView<>();
        ordersData = FXCollections.observableArrayList();

        TableColumn<Order, Integer> orderIdCol = new TableColumn<>("Order ID");
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderIdCol.setPrefWidth(200);

        TableColumn<Order, String> usernameCol = new TableColumn<>("UserId");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        usernameCol.setPrefWidth(200);

        TableColumn<Order, String> shippingStatusCol = new TableColumn<>("Shipping Status");
        shippingStatusCol.setCellValueFactory(new PropertyValueFactory<>("shippingStatus"));
        shippingStatusCol.setPrefWidth(200);

        TableColumn<Order, String> shippingAddressCol = new TableColumn<>("Shipping Address");
        shippingAddressCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
        shippingAddressCol.setPrefWidth(200);

        TableColumn<Order, Void> updateCol = new TableColumn<>("Update Status");
        updateCol.setPrefWidth(200);
        updateCol.setCellFactory(tc -> new TableCell<>() {
            final Button updateButton = new Button("Update");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(updateButton);
                    updateButton.setOnAction(e -> {
                        Order order = getTableView().getItems().get(getIndex());
                        updateShippingStatus(order);
                    });
                }
            }
        });

        ordersTable.setPadding(new Insets(10));
        ordersTable.getColumns().addAll(orderIdCol, usernameCol, shippingStatusCol, shippingAddressCol, updateCol);
        ordersTable.getSelectionModel().selectedItemProperty().addListener((observable, oldOrder, newOrder) -> {
            if (newOrder != null) {
                showOrderItems(newOrder.getOrderId());
            }
        });


        fetchOrdersData();


        orderItemsTable = new TableView<>();
        orderItemsData = FXCollections.observableArrayList();

        TableColumn<OrderItem, String> productNameCol = new TableColumn<>("Product Name");
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productNameCol.setPrefWidth(300);

        TableColumn<OrderItem, Double> productPriceCol = new TableColumn<>("Product Price");
        productPriceCol.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        productPriceCol.setPrefWidth(300);

        orderItemsTable.setPadding(new Insets(10));
        orderItemsTable.getColumns().addAll(productNameCol, productPriceCol);



        VBox layout = new VBox(10, ordersTable, orderItemsTable);
        layout.setPadding(new Insets(20));
        root.setCenter(layout);
        Scene scene = new Scene(root, 1400, 750);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    private void fetchOrdersData() {
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT orders.order_id, orders.user_id, orders.shipping_status, " +
                             "address.street, address.city, address.postal_code, " +
                             "payment.payment_id, payment.method, payment.currency " +
                             "FROM orders " +
                             "JOIN users ON orders.user_id = users.user_id " +
                             "JOIN address ON orders.address_id = address.address_id " +
                             "JOIN payment ON orders.payment_id = payment.payment_id")) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Address address = new Address(
                        rs.getString("street"),
                        rs.getString("city"),
                        rs.getString("postal_code")
                );

                Payment payment = new Payment(
                        rs.getString("method"),
                        rs.getString("currency")
                );

                ordersData.add(new Order(
                        rs.getInt("order_id"),
                        rs.getInt("user_id"),
                        address,
                        payment,
                        rs.getString("shipping_status")
                ));
            }
            ordersTable.setItems(ordersData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showOrderItems(int orderId) {
        orderItemsData.clear();
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT oi.order_id, oi.product_id, p.name AS product_name, p.price AS product_price " +
                             "FROM order_items oi " +
                             "JOIN products p ON oi.product_id = p.product_id " +
                             "WHERE oi.order_id = ?")) {

            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                OrderItem orderItem = new OrderItem(
                        rs.getInt("order_id"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getDouble("product_price")
                );
                orderItemsData.add(orderItem);
            }
            orderItemsTable.setItems(orderItemsData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateShippingStatus(Order order) {
        TextInputDialog dialog = new TextInputDialog(order.getShippingStatus());
        dialog.setTitle("Update Shipping Status");
        dialog.setHeaderText("Order ID: " + order.getOrderId());
        dialog.setContentText("Enter new shipping status (Pending or Shipped):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newStatus -> {

            if (!newStatus.equalsIgnoreCase("Pending") && !newStatus.equalsIgnoreCase("Shipped")) {
                showAlert("Invalid Status", "Please enter either 'Pending' or 'Shipped' as the shipping status.");
                return;
            }

            try (Connection connection = Database.getInstance().getConnection();
                 PreparedStatement stmt = connection.prepareStatement(
                         "UPDATE orders SET shipping_status = ? WHERE order_id = ?")) {

                stmt.setString(1, newStatus);
                stmt.setInt(2, order.getOrderId());
                stmt.executeUpdate();

                order.setShippingStatus(newStatus);
                ordersTable.refresh();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
