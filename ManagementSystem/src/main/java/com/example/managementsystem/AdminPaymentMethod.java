package com.example.managementsystem;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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

public class AdminPaymentMethod  {

    private TableView<Payment> paymentTable = new TableView<>();
    private ObservableList<Payment> paymentList = FXCollections.observableArrayList();

    public AdminPaymentMethod() {
        Stage stage=new Stage();
        stage.setTitle("Payment Management");
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

        // Set up table columns
        TableColumn<Payment, String> methodColumn = new TableColumn<>("Method");
        methodColumn.setCellValueFactory(new PropertyValueFactory<>("method"));
        methodColumn.setPrefWidth(500);

        TableColumn<Payment, String> currencyColumn = new TableColumn<>("Currency");
        currencyColumn.setCellValueFactory(new PropertyValueFactory<>("currency"));
        currencyColumn.setPrefWidth(500);

        paymentTable.getColumns().addAll(methodColumn, currencyColumn);
        paymentTable.setItems(paymentList);


        loadPaymentsFromDatabase();


        TextField methodField = new TextField();
        methodField.setPromptText("Payment Method");

        TextField currencyField = new TextField();
        currencyField.setPromptText("Currency");

        Button addButton = new Button("Add Payment Method");
        addButton.setOnAction(event -> {
            String method = methodField.getText();
            String currency = currencyField.getText();

            try (Connection connection = Database.getInstance().getConnection();
                 PreparedStatement stmt = connection.prepareStatement("INSERT INTO payment (method, currency) VALUES (?, ?)")) {
                stmt.setString(1, method);
                stmt.setString(2, currency);
                stmt.executeUpdate();

                // Add the new payment to the list and update the TableView
                Payment payment = new Payment(method, currency);
                paymentList.add(payment);
                methodField.clear();
                currencyField.clear();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(30, paymentTable, methodField, currencyField, addButton);
        layout.setPadding(new Insets(20));
        layout.setPadding(new Insets(10));
        root.setCenter(layout);
        Scene scene=new Scene(root,1400,750);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private void loadPaymentsFromDatabase() {
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM payment");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String method = rs.getString("method");
                String currency = rs.getString("currency");
                Payment payment = new Payment(method, currency);
                paymentList.add(payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
