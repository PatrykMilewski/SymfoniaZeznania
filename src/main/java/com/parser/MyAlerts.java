package com.parser;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class MyAlerts {
    
    private MyAlerts() { }
    
    public static void showExceptionAlert(String headerText, Exception e, boolean wait) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Wystąpił wyjątek");
            alert.setHeaderText(headerText);
            alert.setContentText(e.getMessage());
            
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String exceptionText = sw.toString();
            
            Label label = new Label("The exception stacktrace was:");
            
            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);
            
            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);
            
            alert.getDialogPane().setExpandableContent(expContent);
            
            showAndWait(alert, wait);
        });
    }
    
    public static void showInfoAlert(String title, String header, String context, boolean wait) {
        Platform.runLater(() -> showAlert(Alert.AlertType.INFORMATION, title, header, context, wait));
    }
    
    public static void showWarningAlert(String title, String header, String context, boolean wait) {
        Platform.runLater(() -> showAlert(Alert.AlertType.WARNING, title, header, context, wait));
    }
    
    private static void showAlert(Alert.AlertType type, String title, String header, String context, boolean wait) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(context);
        
        showAndWait(alert, wait);
    }
    
    private static void showAndWait(Alert alert, boolean wait) {
        if (wait)
            alert.showAndWait();
        else
            alert.show();
    }
    
}