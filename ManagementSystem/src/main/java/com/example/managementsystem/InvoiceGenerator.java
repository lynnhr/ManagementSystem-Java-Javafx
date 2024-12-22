package com.example.managementsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Table;
import org.apache.poi.wp.usermodel.Paragraph;
import org.apache.poi.xwpf.usermodel.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceGenerator {
    public void showInvoice(int orderID) {
        Stage stage = new Stage();
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

        TableView<OrderItem> tableView = new TableView<>();

        TableColumn<OrderItem, Integer> orderIdCol = new TableColumn<>("Order ID");
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderIdCol.setPrefWidth(200);

        TableColumn<OrderItem, String> productNameCol = new TableColumn<>("Product Name");
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productNameCol.setPrefWidth(200);

        TableColumn<OrderItem, Double> productPriceCol = new TableColumn<>("Price");
        productPriceCol.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        productPriceCol.setPrefWidth(550);

        tableView.setPrefSize(100,600);
        tableView.getColumns().addAll(orderIdCol, productNameCol, productPriceCol);


        ObservableList<OrderItem> items = FXCollections.observableArrayList(generateInvoice(orderID));
        tableView.setItems(items);


        double totalPrice = items.stream()
                .mapToDouble(OrderItem::getProductPrice)
                .sum();


        Label totalLabel = new Label("Total Price: $" + totalPrice);
        totalLabel.setScaleX(1);

        VBox vbox2 = new VBox(tableView, totalLabel);
        root.setCenter(vbox2);
        Scene scene = new Scene(root, 1400, 750);
        ThemeManager.registerStage(stage);
        ThemeManager.applyThemeToScene(scene);
        stage.setTitle("Invoice");
        stage.setScene(scene);
        stage.show();
    }
    private List<OrderItem> generateInvoice(int orderId) {
        List<OrderItem> invoiceItems = new ArrayList<>();
        String query = """
            SELECT 
                oi.order_id,
                oi.product_id,
                p.name AS product_name,
                p.price AS product_price
            FROM 
                orders o
            JOIN 
                order_items oi ON o.order_id = oi.order_id
            JOIN 
                products p ON oi.product_id = p.product_id
            WHERE 
                o.order_id = ?;
        """;

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int oid = rs.getInt("order_id");
                int productId = rs.getInt("product_id");
                String productName = rs.getString("product_name");
                double productPrice = rs.getDouble("product_price");

                invoiceItems.add(new OrderItem(oid, productId, productName, productPrice));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return invoiceItems;
    }

}

