package com.example.managementsystem;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.sql.SQLException;

public class Signup  {
    private TextField usernameField;
    private PasswordField passwordField;
    private Button submitButton;

    public Signup() {
        Stage stage=new Stage();

        ImageView imageView=new ImageView("/logo.png");
        imageView.setX(620);
        imageView.setLayoutY(200);

        Label usernameLabel = new Label("Username:");
        usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        passwordField = new PasswordField();

        submitButton = new Button("Submit");


        Label label1=new Label("Already have an account?");
        Label label2=new Label("Click Here.");

        HBox hBox=new HBox(label1,label2);
        VBox vBox=new VBox(usernameLabel,usernameField,passwordLabel,passwordField,hBox,submitButton);
        vBox.setPadding(new Insets(10));
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(15);
        vBox.setLayoutX(570);
        vBox.setLayoutY(350);

        Rectangle rectangle=new Rectangle(450,150,500,500);
        rectangle.setFill(Color.WHITE);

        label2.setOnMouseClicked(e->{
                Login login=new Login();
                stage.close();
                });
        submitButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showAlert("Please fill in both username and password.");
                return;
            }

            try {

                UserCRUD userCRUD = UserCRUD.getUserCRUD();
                if (userCRUD.isUsernameTaken(username)) {
                    showAlert("Username already taken. Please choose a different one.");
                    return;
                }


                userCRUD.createUserFn(username, password);
                showAlert("Signup successful!");


                DisplayProducts displayProducts = new DisplayProducts();
                displayProducts.showProducts();

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error occurred while processing your request.");
            }
        });



        Pane pane=new Pane(rectangle,vBox,imageView);
        pane.setBackground(new Background(new BackgroundFill(Color.web("#86b499"), CornerRadii.EMPTY, null)));
        Scene scene=new Scene(pane,1400,750);


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

    public TextField getUsernameField() {
        return usernameField;
    }

    public TextField getPasswordField() {
        return passwordField;
    }

    public Button getSubmitButton() {
        return submitButton;
    }
}
