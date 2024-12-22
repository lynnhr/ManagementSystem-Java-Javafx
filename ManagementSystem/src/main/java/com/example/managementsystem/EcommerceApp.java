package com.example.managementsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class EcommerceApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        WelcomeStage welcomeStage=new WelcomeStage();
    }

    public static void main(String[] args) {
        launch();
    }
}