package com.example.managementsystem;
import com.example.managementsystem.ShoppingCart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
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
import javafx.util.Callback;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DisplayCart {
    public void showCart() throws SQLException {
        Stage stage = new Stage();
        stage.setTitle("My Cart");

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
//
            });

        }
        AnchorPane pane=new AnchorPane(side,vbox);
        root.setLeft(pane);


        TableView<ShoppingCart> tableView = new TableView<>();
        ObservableList<ShoppingCart> cartItems = FXCollections.observableArrayList();


        // Define columns
        TableColumn<ShoppingCart, String> nameColumn = new TableColumn<>("Product Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(200);

        TableColumn<ShoppingCart, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceColumn.setPrefWidth(150);

        TableColumn<ShoppingCart, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityColumn.setPrefWidth(150);

        TableColumn<ShoppingCart, Double> totalColumn = new TableColumn<>("Total Price");
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        totalColumn.setPrefWidth(150);

        // Add a Remove Button column
        TableColumn<ShoppingCart, Void> removeColumn = new TableColumn<>("Actions");
        removeColumn.setCellFactory(getRemoveButtonCellFactory(cartItems, tableView));
        removeColumn.setPrefWidth(300);

        tableView.getColumns().addAll(nameColumn, priceColumn, quantityColumn, totalColumn, removeColumn);

        // Fetch data from database and populate TableView
        CartCRUD cartCRUD = new CartCRUD();
        try (ResultSet rs = cartCRUD.getCart()) {
            while (rs.next()) {
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                double totalPrice = rs.getDouble("total_price");

                cartItems.add(new ShoppingCart(name, price, quantity, totalPrice));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tableView.setItems(cartItems);
        tableView.setPrefSize(1300,700);

        Button placeOrder = new Button("Place an order");
        placeOrder.setOnAction(e -> {
            OrderUser orderUser = new OrderUser();
            orderUser.showOrderForm();
            stage.close();
        });
        placeOrder.setPadding(new Insets(20));
        placeOrder.setLayoutX(800);
        placeOrder.setLayoutY(700);

        AnchorPane panee=new AnchorPane(tableView,placeOrder);
        root.setCenter(panee);
        Scene scene = new Scene(root, 1400, 750);
        ThemeManager.registerStage(stage);
        ThemeManager.applyThemeToScene(scene);
        stage.setScene(scene);
        stage.show();
    }

    private Callback<TableColumn<ShoppingCart, Void>, TableCell<ShoppingCart, Void>> getRemoveButtonCellFactory(ObservableList<ShoppingCart> cartItems, TableView<ShoppingCart> tableView) {
        return param -> new TableCell<>() {
            private final Button removeButton = new Button("Remove");

            {
                removeButton.setOnAction(event -> {
                    ShoppingCart cartItem = getTableView().getItems().get(getIndex());
                    try {
                        CartCRUD cartCRUD = new CartCRUD();
                        cartCRUD.removeFromCart(cartItem.getName());
                        cartItems.remove(cartItem); // Update the observable list
                        tableView.refresh();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(removeButton);
                }
            }
        };
    }
}
