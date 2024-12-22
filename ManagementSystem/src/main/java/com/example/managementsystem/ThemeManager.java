package com.example.managementsystem;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {

    // List to track all stages in the application
    private static final List<Stage> allStages = new ArrayList<>();

    // Apply the dark/light theme to the scene
    public static void applyThemeToScene(Scene scene) {
        User user = User.getUser();

        // Clear existing stylesheets and apply the selected theme
        scene.getStylesheets().clear();

        if (user.isDarkMode()) {
            scene.getStylesheets().add(ThemeManager.class.getResource("/darkmode.css").toExternalForm());
        } else {
            scene.getStylesheets().add(ThemeManager.class.getResource("/styles.css").toExternalForm());
        }
    }

    // Add a stage to the list
    public static void registerStage(Stage stage) {
        allStages.add(stage);
    }

    // Apply theme to all stages
    public static void applyThemeToAllStages() {
        // Iterate over all tracked stages
        for (Stage stage : allStages) {
            Scene scene = stage.getScene();
            if (scene != null) {
                applyThemeToScene(scene);  // Apply the theme to the scene
            }
        }
    }

    // Call this method to ensure all stages are updated after toggling dark mode
    public static void updateThemeAfterToggle() {
        // Apply theme to all stages
        applyThemeToAllStages();
    }
    public static void applyDarkTheme() {
        for (Stage stage : Stage.getWindows().stream().map(window -> (Stage) window).toList()) {
            stage.getScene().getStylesheets().clear();
            stage.getScene().getStylesheets().add("/dark_theme.css");
        }
    }

    public static void applyLightTheme() {
        for (Stage stage : Stage.getWindows().stream().map(window -> (Stage) window).toList()) {
            stage.getScene().getStylesheets().clear();
            stage.getScene().getStylesheets().add("/styles.css");
        }
    }

}
