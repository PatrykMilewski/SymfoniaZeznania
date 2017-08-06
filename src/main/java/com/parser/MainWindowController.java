package com.parser;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainWindowController {
    private Stage stage;
    
    List<File> files;
    
    @FXML
    private ListView<XmlFileOnList> itemsList;
    
    private List<XmlFileOnList> xmlFiles;
    private boolean filesSelected = false;
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public void closeApplication() {
    
    }
    
    @FXML
    public void selectFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML documents", "*.xml"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files", "*.*"));
        files = fileChooser.showOpenMultipleDialog(null);
        if (files != null && !files.isEmpty()) {
            filesSelected = true;
            xmlFiles = new ArrayList<>(files.size());
            files.forEach(file -> xmlFiles.add(new XmlFileOnList(file)));
            itemsList.getItems().addAll(xmlFiles);
        }
    }
    
    @FXML
    public void convert() {
        if (!filesSelected)
            return;
    
        SAXBuilder builder = new SAXBuilder();
        for (XmlFileOnList xmlFile : xmlFiles) {
            try {
                Document document = builder.build(xmlFile.getFile());
                
                
            } catch (JDOMException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
