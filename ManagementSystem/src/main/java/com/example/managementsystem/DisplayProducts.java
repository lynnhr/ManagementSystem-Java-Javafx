package com.example.managementsystem;

import java.sql.*;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;

public class DisplayProducts {

    private PreparedStatement fetchProducts;

    public DisplayProducts() throws SQLException {
        fetchProducts = Database.getInstance().getConnection().prepareStatement("SELECT * FROM products");
    }

    public void showProducts() {
        try (ResultSet rs = fetchProducts.executeQuery()) {
            Stage stage = new Stage();
            stage.setTitle("Product List");

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


            GridPane grid = new GridPane();
            grid.setHgap(20); // Horizontal spacing between elements
            grid.setVgap(30); // Vertical spacing between elements
            grid.setPadding(new javafx.geometry.Insets(20)); // Padding around the grid




            int row = 0;
            int col = 0;

            while (rs.next()) {
                int productId = rs.getInt("product_id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                String description = rs.getString("description");
                String imagePath = rs.getString("image_path");

                // Create and style the product layout (rectangle)
                VBox productBox = new VBox(10);
                productBox.setPadding(new Insets(30));
               // productBox.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(5), Insets.EMPTY)));


                // Load image
                Image image = new Image("/"+imagePath);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(200);
                imageView.setFitHeight(200);
                imageView.setPreserveRatio(true);

                // Create text elements for name, price, and description
                Text nameText = new Text( name);
                Text priceText = new Text("Price: $" + String.format("%.2f", price));
                Text descriptionText = new Text("Description: " + description);

                nameText.setFill(Color.web("#1e6234"));
                nameText.setFont(Font.font("Calibri Bold", FontWeight.BOLD, 30));
                 priceText.setFill(Color.web("#1e6234"));
                priceText.setFont(Font.font("Calibri Bold", 25));
                 descriptionText.setFill(Color.web("#1e6234"));
                descriptionText.setFont(Font.font("Calibri Bold", 25));


                // Create add to cart button
                Button addButton = new Button("Add to Cart");
                addButton.setOnAction(e -> {
                    CartCRUD cartCRUD = null;
                    try {
                        cartCRUD = new CartCRUD();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    cartCRUD.addToCart(productId, 1); // Adds 1 quantity of the product
                    System.out.println("Added " + name + " to cart.");
                });

                // Add elements to product layout
                productBox.getChildren().addAll(imageView, nameText, priceText, descriptionText, addButton);

                // Add productBox to the grid
                grid.add(productBox, col, row);

                // Move to the next column and start a new row if needed
                col++;
                if (col > 2) { // Adjust the number of products per row (e.g., 3 columns)
                    col = 0;
                    row++;
                }
            }
            ;
            ComboBox<String> categoryComboBox = new ComboBox<>();
            categoryComboBox.setPromptText("Select Category");
            categoryComboBox.setPadding(new Insets(10));

            categoryComboBox.setOnAction(e -> refreshProducts(grid, categoryComboBox.getValue()));
            try (Connection conn = Database.getInstance().getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet resultSet = stmt.executeQuery("SELECT DISTINCT category FROM products")) {
                while (resultSet.next()) {
                    categoryComboBox.getItems().add(resultSet.getString("category"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            ScrollPane s1 = new ScrollPane();
            //s1.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            s1.setPrefSize(1400, 800);
            s1.setContent(grid);

            VBox topBox = new VBox( categoryComboBox,s1);
            //topBox.setAlignment(Pos.TOP_CENTER);
            //topBox.setLayoutX(1000);
            //topBox.setLayoutY(100);
            //root.setRight(topBox);

            root.setCenter(topBox);


            Scene scene = new Scene(root, 1400, 750);
            //scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            stage.setScene(scene);
            ThemeManager.registerStage(stage);
            ThemeManager.applyThemeToScene(scene);
            stage.show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void refreshProducts(GridPane grid, String category) {
        grid.getChildren().clear(); // Clear the grid before refreshing
        try {
            if (category == null) {
                fetchProducts = Database.getInstance().getConnection().prepareStatement("SELECT * FROM products");
            } else {
                fetchProducts = Database.getInstance().getConnection().prepareStatement("SELECT * FROM products WHERE category = ?");
                fetchProducts.setString(1, category);
            }

            try (ResultSet rs = fetchProducts.executeQuery()) {
                int row = 0, col = 0;

                while (rs.next()) {
                    int productId = rs.getInt("product_id");
                    String name = rs.getString("name");
                    double price = rs.getDouble("price");
                    String description = rs.getString("description");
                    String imagePath = rs.getString("image_path");

                    VBox productBox = new VBox(10);
                    productBox.setPadding(new Insets(30));

                    Image image = new Image("/" + imagePath);
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(200);
                    imageView.setFitHeight(200);
                    imageView.setPreserveRatio(true);

                    Text nameText = new Text(name);
                    Text priceText = new Text("Price: $" + String.format("%.2f", price));
                    Text descriptionText = new Text("Description: " + description);

                    nameText.setFill(Color.web("#1e6234"));
                    nameText.setFont(Font.font("Calibri Bold", FontWeight.BOLD, 30));
                    priceText.setFill(Color.web("#1e6234"));
                    priceText.setFont(Font.font("Calibri Bold", 25));
                    descriptionText.setFill(Color.web("#1e6234"));
                    descriptionText.setFont(Font.font("Calibri Bold", 25));

                    Button addButton = new Button("Add to Cart");
                    addButton.setOnAction(e -> {
                        CartCRUD cartCRUD;
                        try {
                            cartCRUD = new CartCRUD();
                            cartCRUD.addToCart(productId, 1);
                            System.out.println("Added " + name + " to cart.");
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    });

                    productBox.getChildren().addAll(imageView, nameText, priceText, descriptionText, addButton);
                    grid.add(productBox, col, row);

                    col++;
                    if (col > 2) {
                        col = 0;
                        row++;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
