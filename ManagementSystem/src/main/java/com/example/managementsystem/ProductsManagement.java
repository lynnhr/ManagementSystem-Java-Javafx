package com.example.managementsystem;


import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ProductsManagement  {
    private TableView<Product> productTable = new TableView<>();
    private ProductsCRUD productService = new ProductsCRUD();

    public ProductsManagement() {
        Stage stage=new Stage();
        stage.setTitle("Product Management");

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



        TableColumn<Product, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(200);

        TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceColumn.setPrefWidth(150);

        TableColumn<Product, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setPrefWidth(250);

        TableColumn<Product, Integer> stockColumn = new TableColumn<>("Stock");
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));
        stockColumn.setPrefWidth(150);

        TableColumn<Product, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryColumn.setPrefWidth(200);

        productTable.setPadding(new Insets(8));
        productTable.getColumns().addAll(nameColumn, priceColumn, descriptionColumn, stockColumn, categoryColumn);


        Button addButton = new Button("Add");
        addButton.setOnAction(e->{
            openAddDialog();
        });


        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                productService.deleteProduct(selectedProduct);
                refreshTable();
            }
            else{
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a product to Delete.");
                alert.showAndWait();
            }
        });
        Button updateButton = new Button("Update");
        updateButton.setOnAction(e -> {
            Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                openUpdateDialog(selectedProduct);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a product to update.");
                alert.showAndWait();
            }
        });


        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> refreshTable());


        HBox buttonBox = new HBox(10, addButton, deleteButton, updateButton, refreshButton);


        BorderPane root2= new BorderPane();
        root2.setCenter(productTable);
        root2.setBottom(buttonBox);
        root.setCenter(root2);
        Scene scene=new Scene(root, 1400, 750);

        stage.setScene(scene);
        stage.show();
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        refreshTable();
    }


    public void refreshTable() {
        productTable.getItems().clear();
        productTable.getItems().addAll(productService.getAllProducts());
    }

    private void openAddDialog() {
        Stage addDialog = new Stage();
        addDialog.setTitle("Add Product");

        TextField nameField = new TextField();
        TextField priceField = new TextField();
        TextField descriptionField = new TextField();
        TextField stockField = new TextField();
        TextField categoryField = new TextField();
        TextField imagePathField = new TextField();

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            String name = nameField.getText();
            String priceText = priceField.getText();
            String description = descriptionField.getText();
            String stockText = stockField.getText();
            String category = categoryField.getText().trim().toLowerCase();
            String imagePath = imagePathField.getText();

            // Validation
            if (name.isEmpty() || priceText.isEmpty() || description.isEmpty() ||
                    stockText.isEmpty() || category.isEmpty() || imagePath.isEmpty()) {
                showAlert("Validation Error", "All fields must be filled.");
                return;
            }

            if (!priceText.matches("\\d+(\\.\\d+)?") || Double.parseDouble(priceText) <= 0) {
                showAlert("Validation Error", "Price must be a positive number.");
                return;
            }

            if (!stockText.matches("\\d+") || Integer.parseInt(stockText) <= 0) {
                showAlert("Validation Error", "Stock Quantity must be a positive integer.");
                return;
            }

            if (!category.equals("racket") && !category.equals("ball") && !category.equals("hat")) {
                showAlert("Validation Error", "Category must be 'Racket', 'Ball', or 'Hat'.");
                return;
            }

            // If validation passes
            try {
                String nameValidated = name;
                double price = Double.parseDouble(priceText);
                String descriptionValidated = description;
                int stockQuantity = Integer.parseInt(stockText);
                String categoryValidated = category;
                String imagePathValidated = imagePath;

                Product product = new Product(nameValidated, price, descriptionValidated, stockQuantity, categoryValidated, imagePathValidated);

                // Save changes to the database
                productService.addProduct(product);
                refreshTable();
                addDialog.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Error", "Failed to add product. Please try again.");
            }
        });

        VBox dialogLayout = new VBox(10,
                new Label("Name:"), nameField,
                new Label("Price:"), priceField,
                new Label("Description:"), descriptionField,
                new Label("Stock Quantity:"), stockField,
                new Label("Category:"), categoryField,
                new Label("Image Path:"), imagePathField,
                saveButton
        );
        dialogLayout.setPadding(new Insets(10));

        Scene scene = new Scene(dialogLayout, 500, 500);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        addDialog.setScene(scene);
        addDialog.show();
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openUpdateDialog(Product product) {
        Stage dialog = new Stage();
        dialog.setTitle("Update Product");

        // Form fields
        TextField nameField = new TextField(product.getName());
        TextField priceField = new TextField(String.valueOf(product.getPrice()));
        TextField descriptionField = new TextField(product.getDescription());
        TextField stockField = new TextField(String.valueOf(product.getStockQuantity()));
        TextField categoryField = new TextField(product.getCategory());
        TextField imagePathField = new TextField(product.getImagePath());

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            String name = nameField.getText();
            String priceText = priceField.getText();
            String description = descriptionField.getText();
            String stockText = stockField.getText();
            String category = categoryField.getText().trim().toLowerCase();
            String imagePath = imagePathField.getText();

            // Validation
            if (name.isEmpty() || priceText.isEmpty() || description.isEmpty() ||
                    stockText.isEmpty() || category.isEmpty() || imagePath.isEmpty()) {
                showAlert("Validation Error", "All fields must be filled.");
                return;
            }

            if (!priceText.matches("\\d+(\\.\\d+)?") || Double.parseDouble(priceText) <= 0) {
                showAlert("Validation Error", "Price must be a positive number.");
                return;
            }

            if (!stockText.matches("\\d+") || Integer.parseInt(stockText) <= 0) {
                showAlert("Validation Error", "Stock Quantity must be a positive integer.");
                return;
            }

            if (!category.equals("racket") && !category.equals("ball") && !category.equals("hat")) {
                showAlert("Validation Error", "Category must be 'Racket', 'Ball', or 'Hat'.");
                return;
            }

            // If validation passes
            try {
                product.setName(name);
                product.setPrice(Double.parseDouble(priceText));
                product.setDescription(description);
                product.setStockQuantity(Integer.parseInt(stockText));
                product.setCategory(category);
                product.setImagePath(imagePath);

                // Save changes to the database
                productService.updateProduct(product);
                refreshTable();
                dialog.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Error", "Failed to update product. Please try again.");
            }
        });

        VBox dialogLayout = new VBox(10,
                new Label("Name:"), nameField,
                new Label("Price:"), priceField,
                new Label("Description:"), descriptionField,
                new Label("Stock Quantity:"), stockField,
                new Label("Category:"), categoryField,
                new Label("Image Path:"), imagePathField,
                saveButton
        );
        dialogLayout.setPadding(new Insets(10));
        Scene scene = new Scene(dialogLayout, 500, 500);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        dialog.setScene(scene);
        dialog.show();
    }

}

