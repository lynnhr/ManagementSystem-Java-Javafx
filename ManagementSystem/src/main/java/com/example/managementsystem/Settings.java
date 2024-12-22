package com.example.managementsystem;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class Settings {
    private User user = User.getUser(); // Access the singleton instance of User

    public Settings() {
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

        Rectangle rectangle = new Rectangle(200, 150, 500, 500);
        rectangle.setStroke(Color.web("#86b499"));
        rectangle.setStrokeWidth(2);
        rectangle.setFill(Color.TRANSPARENT);




        Label label = new Label("Choose Theme: ");
        label.setStyle("-fx-font-size: 30px;");

        RadioButton darkModeRadio = new RadioButton("Dark Mode");
        darkModeRadio.setStyle("-fx-font-size: 20px;");

        RadioButton lightModeRadio = new RadioButton("Light Mode");
        lightModeRadio.setStyle("-fx-font-size: 20px;");

        VBox vBox2 = new VBox(30, label, darkModeRadio, lightModeRadio);
        vBox2.setSpacing(40);
        vBox2.setPadding(new Insets(30));
        vBox2.setLayoutY(300);
        vBox2.setLayoutX(320);
        vBox2.setAlignment(Pos.CENTER);


        Pane layout=new Pane(rectangle,vBox2);

        ToggleGroup toggleGroup = new ToggleGroup();
        darkModeRadio.setToggleGroup(toggleGroup);
        lightModeRadio.setToggleGroup(toggleGroup);


        if (user.isDarkMode()) {
            darkModeRadio.setSelected(true);
        } else {
            lightModeRadio.setSelected(true);
        }


        darkModeRadio.setOnAction(event -> {
            user.setDarkMode(true); // Dark Mode selected
            ThemeManager.updateThemeAfterToggle();
            savePreferences();
        });

        lightModeRadio.setOnAction(event -> {
            user.setDarkMode(false);
            ThemeManager.updateThemeAfterToggle();
            savePreferences();
        });


        root.setCenter(layout);

        Scene scene = new Scene(root, 1400, 750);
        ThemeManager.registerStage(stage);
        ThemeManager.applyThemeToScene(scene);
        stage.setTitle("Theme Preferences");
        stage.setScene(scene);
        stage.show();
    }

    public void savePreferences() {
        User user = User.getUser();
        File file = new File("user_preferences.csv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write the header
            writer.write("user_id,dark_mode");
            writer.newLine();

            // Write the user's preferences
            writer.write(user.getUserID() + "," + user.isDarkMode());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPreferences() {
        User user = User.getUser();
        File file = new File("user_preferences.csv");

        if (file.exists()) {
            try (Scanner scanner = new Scanner(file)) {
                // Skip header line
                if (scanner.hasNextLine()) {
                    scanner.nextLine(); // Skip "user_id,dark_mode" header
                }

                while (scanner.hasNextLine()) {
                    String[] preference = scanner.nextLine().split(",");
                    if (preference.length == 2 && preference[0].equals(String.valueOf(user.getUserID()))) {
                        user.setDarkMode(Boolean.parseBoolean(preference[1]));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void applyThemeAfterLoad() {
        // Apply the theme globally after loading preferences
        User user = User.getUser();
        if (user.isDarkMode()) {
            ThemeManager.applyDarkTheme(); // Custom method in your ThemeManager
        } else {
            ThemeManager.applyLightTheme(); // Custom method in your ThemeManager
        }
    }




    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Preferences Saved");
        alert.setHeaderText("Your preferences have been saved.");
        alert.showAndWait();
    }
}
