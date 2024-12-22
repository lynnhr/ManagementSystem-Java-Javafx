package com.example.managementsystem;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.awt.Desktop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



public class OrderHistory {
    public OrderHistory() {
        Stage stage=new Stage();
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

        TableView<Order> tableView = new TableView<>();


        TableColumn<Order, Integer> orderIdCol = new TableColumn<>("Order ID");
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderIdCol.setPrefWidth(200);

        TableColumn<Order, Integer> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userIdCol.setPrefWidth(200);

        TableColumn<Order, String> shippingStatusCol = new TableColumn<>("Shipping Status");
        shippingStatusCol.setCellValueFactory(new PropertyValueFactory<>("shippingStatus"));
        shippingStatusCol.setPrefWidth(550);

        tableView.setPadding(new Insets(10));
        tableView.setPrefSize(100,600);
        tableView.setPadding(new Insets(10));
        tableView.getColumns().addAll(orderIdCol, userIdCol, shippingStatusCol);
        int userId=User.getUser().getUserID();


        List<Order> orders = fetchOrdersByUserId(userId);
        tableView.getItems().addAll(orders);
        Button excel=new Button("Download as Excel File");
        excel.setPadding(new Insets(20));
        excel.setOnAction(e->exportToExcel(orders));
        VBox vBox=new VBox(tableView,excel);

        root.setCenter(vBox);


        Scene scene = new Scene(root, 1400, 750);
        ThemeManager.registerStage(stage);
        ThemeManager.applyThemeToScene(scene);
        stage.setScene(scene);
        stage.setTitle("Orders by User");
        stage.show();
    }

    private List<Order> fetchOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection()) {
            String query = "SELECT * FROM orders WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt("order_id"),
                        rs.getInt("user_id"),
                        rs.getString("shipping_status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    public void exportToExcel(List<Order> orders) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Orders");


        Row headerRow = sheet.createRow(0);
        String[] headers = {"Order ID", "User ID", "Shipping Status"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }


        int rowIndex = 1;
        for (Order order : orders) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(order.getOrderId());
            row.createCell(1).setCellValue(order.getUserId());
            row.createCell(2).setCellValue(order.getShippingStatus());
        }


        File file = new File("orders.xlsx");
        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);


            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Orders exported to Excel successfully. Open the file?", ButtonType.YES, ButtonType.NO);
            alert.setHeaderText(null);
            alert.setTitle("Export Complete");


            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    openFile(file);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openFile(File file) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Desktop is not supported. Cannot open the file.");
        }
    }
}



