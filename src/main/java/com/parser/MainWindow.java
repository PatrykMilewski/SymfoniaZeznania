package com.parser;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWindow extends Application {
    
    private Stage stage;
    private MainWindowController controller;
    
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/mainWindow.fxml"));
        this.stage = primaryStage;
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        controller.setStage(stage);
        stage.setScene(new Scene(root));
        stage.setTitle("Symfonia konwerter zeznań");
        stage.show();
    }
    
    @Override
    public void stop() {
        if (controller != null)
            controller.closeApplication();
        System.exit(0);
    }
    
    public static void main(String args[]) {
        launch();
    }
}
