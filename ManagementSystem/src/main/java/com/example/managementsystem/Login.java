package com.example.managementsystem;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Login {
    private TextField userNameField;
    private PasswordField passwordField;
    private Button submitButton;

    public Login() {

        ImageView imageView=new ImageView("/logo.png");
        imageView.setX(620);
        imageView.setLayoutY(200);

        Label firstNameLabel = new Label("Username:");
        userNameField = new TextField();

        Label lastNameLabel = new Label("Password:");
        passwordField = new PasswordField();

        submitButton = new Button("Submit");
        VBox vBox=new VBox(firstNameLabel,userNameField,lastNameLabel,passwordField,submitButton);
        vBox.setPadding(new Insets(10));
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(15);
        vBox.setLayoutX(580);
        vBox.setLayoutY(350);


        Rectangle rectangle=new Rectangle(450,150,500,500);
        rectangle.setFill(Color.WHITE);

        // Add Event Handling
        submitButton.setOnAction(event -> {

            String userName = userNameField.getText();
            String password = passwordField.getText();
            if (userName.isEmpty() || password.isEmpty()) {
                showAlert("Please fill in both username and password.");
                return;
            }

            try {
                UserCRUD userCRUD = UserCRUD.getUserCRUD();
                boolean isLoggedIn = userCRUD.LoggedInUser(userName, password);
                if (isLoggedIn) {
                    User user=User.getUser();
                    String role=user.getRole();
                    System.out.println("Welcome, " + user.getUsername() + "! Login successful.");

                    if (role != null && role.equals("client")) {
                        System.out.println("User is a client");
                        DisplayProducts displayProducts=new DisplayProducts();
                        displayProducts.showProducts();
                    } else if (role != null && role.equals("admin")) {
                        System.out.println("User is an admin");
                        ProductsManagement productsManagement=new ProductsManagement();
                    } else {
                        System.out.println("Unexpected role value: " + role);
                    }
                } else {
                    System.out.println("Login failed. Invalid username or password.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

         Pane pane=new Pane(rectangle,vBox,imageView);
         pane.setBackground(new Background(new BackgroundFill(Color.web("#86b499"), CornerRadii.EMPTY, null)));
        Scene scene=new Scene(pane,1400,750);

        Stage stage=new Stage();
        stage.setScene(scene);
        stage.show();
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

    }
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public TextField getFirstNameField() {
        return  userNameField;
    }

    public TextField getLastNameField() {
        return passwordField;
    }

    public Button getSubmitButton() {
        return submitButton;
    }

}


