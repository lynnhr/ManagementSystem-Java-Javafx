package com.example.managementsystem;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class WelcomeStage {
    public WelcomeStage() {
        Stage stage = new Stage();
        Button btn = new Button("Sign Up");
        btn.setLayoutX(300); // Set the x-coordinate
        btn.setLayoutY(550); // Set the y-coordinate

        ImageView imageView = new ImageView("/welcome.png");
        imageView.setFitHeight(750);
        imageView.setFitWidth(1400);
        btn.setOnAction(e->{
            Signup signup=new Signup();

            stage.close();
        });

        // Create a pane and add the image and button to it
        Pane pane = new Pane();
        pane.getChildren().addAll(imageView, btn);

        // Set the button on top of the image view, ensuring correct stacking order
        Scene scene = new Scene(pane, 1400, 750);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }
}
