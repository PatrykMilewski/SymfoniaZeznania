package com.parser;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MainWindowController {
    
    private final static Path SELECTOR_START_FILE_PATH = Paths.get("C:\\dane\\Intrastat");
    
    private Stage stage;
    private Map<SingleRow, HashSet<Element>> elementsMap;
    
    @FXML
    private ListView<XmlFileOnList> itemsList;
    
    private List<XmlFileOnList> xmlFiles;
    private boolean filesSelected = false;
    private Integer lacznaWartoscFaktur;
    
    void setStage(Stage stage) {
        this.stage = stage;
    }
    
    void closeApplication() {
        stage.close();
    }
    
    @FXML
    public void selectFiles() {
        FileChooser fileChooser = new FileChooser();
        
        if (Files.exists(SELECTOR_START_FILE_PATH))
            fileChooser.setInitialDirectory(SELECTOR_START_FILE_PATH.toFile());
        
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML documents", "*.xml"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files", "*.*"));
        List<File> files = fileChooser.showOpenMultipleDialog(null);
        
        // check if user selected any files, if yes then preparing program for conversion
        if (files != null && !files.isEmpty()) {
            filesSelected = true;
            
            // list of xml files to convert
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
        
        // do for every xml file selected
        for (XmlFileOnList xmlFile : xmlFiles) {
            elementsMap = new HashMap<>();
            try {
                Document document = builder.build(xmlFile.getFile());
                
                // get root element from xml file
                Element root = document.getRootElement();
                Namespace rootNamespace = root.getNamespace();
                
                // get Deklaracja child
                Element deklaracja = root.getChild("Deklaracja", rootNamespace);
                Namespace deklaracjaNamespace = deklaracja.getNamespace();
                
                // get all children of Deklaracja, whose name is Towar
                List<Element> towary = deklaracja.getChildren("Towar", deklaracjaNamespace);
                
                // grouping children with the same IdKontrahenta and KodTowarowy attributes into one set
                for (Element element : towary) {
                    SingleRow singleRow = new SingleRow(element.getAttributeValue("IdKontrahenta"),
                            element.getAttributeValue("KodTowarowy"));
                    
                    // if map already contains one
                    if (elementsMap.containsKey(singleRow)) {
                        Set<Element> singleSet = elementsMap.get(singleRow);
                        if (!singleSet.contains(element))
                            singleSet.add(element);
                    }
                    // if a new found
                    else {
                        HashSet<Element> newSet = new HashSet<>();
                        newSet.add(element);
                        elementsMap.put(singleRow, newSet);
                    }
                }
    
                // comparator for having xml elements sorted by PozId attribute
                TreeSet<Element> compressedElements = new TreeSet<>((o1, o2) -> {
                    Integer first = Integer.parseInt(o1.getAttributeValue("PozId"));
                    Integer second = Integer.parseInt(o2.getAttributeValue("PozId"));
                    return first.compareTo(second);
                });
    
                // converting every single set with multipile Towar xml elements to single Towar xml element
                Integer pozId = 1;
                lacznaWartoscFaktur = 0;
                for (Map.Entry<SingleRow, HashSet<Element>> entry : elementsMap.entrySet()) {
                    compressedElements.add(sumElements(entry.getValue(), pozId));
                    pozId++;
                }
                pozId--;
                
                // set value of LacznaWartoscFaktur attribute
                deklaracja.getAttribute("LacznaWartoscFaktur").setValue(lacznaWartoscFaktur.toString());
    
                // set value of LacznaLiczbaPozycji attribute
                deklaracja.getAttribute("LacznaLiczbaPozycji").setValue(pozId.toString());
                
                // remove all children and then add converted one, to avoid unneeded children
                deklaracja.removeChildren("Towar", deklaracjaNamespace);
                deklaracja.addContent(compressedElements);
                
                // prepare path for converted xml file
                XMLOutputter xmlOutputter = new XMLOutputter();
                StringBuilder sb = new StringBuilder();
                sb.append(document.getBaseURI().replaceFirst("file:/", ""));
                String newXmlFilePath = sb.toString();
                
                // save xml file
                Writer fileWriter = new OutputStreamWriter(new FileOutputStream(newXmlFilePath), StandardCharsets.UTF_8);
                xmlOutputter.setFormat(Format.getPrettyFormat());
                xmlOutputter.output(document, fileWriter);
                fileWriter.flush();
                fileWriter.close();
    
                // set xmlFile as successfully converted
                xmlFile.setIsTransformed(true);
            }
            catch (JDOMException | IOException e) {
                MyAlerts.showExceptionAlert("Wystąpił wyjątek podczas przetwarzania pliku xml.", e, false);
                e.printStackTrace();
            }
        }
        
        // refresh list view with new states of files (converted or not)
        itemsList.getItems().removeAll(xmlFiles);
        itemsList.getItems().addAll(xmlFiles);
    }
    
    // sum elements in sets by xml attributes
    private Element sumElements(HashSet<Element> set, Integer pozId) {
    
        Integer wartoscFakturyInt, massaNetto = 0, iloscUzupelniajacaJm = 0;
        Double wartoscFaktury = 0.0;
        
        Element firstElement = set.iterator().next();
        firstElement.getAttribute("PozId").setValue(pozId.toString());
        
        for (Element element : set) {
            wartoscFaktury += Double.parseDouble(element.getAttributeValue("WartoscFaktury"));
            massaNetto += Integer.parseInt(element.getAttributeValue("MasaNetto"));
            iloscUzupelniajacaJm += Integer.parseInt(element.getAttributeValue("IloscUzupelniajacaJm"));
        }
        
        wartoscFakturyInt = round(wartoscFaktury).intValue();
        lacznaWartoscFaktur += wartoscFakturyInt;
        firstElement.getAttribute("WartoscFaktury").setValue(wartoscFakturyInt.toString());
        firstElement.getAttribute("MasaNetto").setValue(massaNetto.toString());
        firstElement.getAttribute("IloscUzupelniajacaJm").setValue(iloscUzupelniajacaJm.toString());
        
        return firstElement;
    }
    
    // simple round function
    private static Double round(Double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(0, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
