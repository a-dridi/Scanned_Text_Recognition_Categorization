/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import at.adridi.scannedtextrecognitioncategorisation.ScannedDocumentContentController;
import db.AppLocalDatabase;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import services.TextRecognition;

/**
 * Table Model for Scanned Document - Main Window
 *
 * @author A.Dridi
 */
public class ViewScannedDocument implements Serializable {

    private int id;
    private String title;
    private String documentDate;
    private String languageName;
    private Button viewDocument;
    private Button pdfDocument;

    public ViewScannedDocument() {

    }

    public ViewScannedDocument(int id, String title, String documentDate, String languageName, String viewDocumentDescription, String pdfDocumentDescription) {
        this.id = id;
        this.title = title;
        this.documentDate = documentDate;
        this.languageName = languageName;
        this.viewDocument = viewDocument;
        this.pdfDocument = pdfDocument;

        this.pdfDocument = new Button(pdfDocumentDescription);
        this.pdfDocument.setId(String.valueOf(this.id));
        this.viewDocument = new Button(viewDocumentDescription);
        this.viewDocument.setId(String.valueOf(this.id));

        //Open pdf file of selected ScannedDocument
        this.pdfDocument.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent clickEvent) {
                Button selectedScannedDocumentButton = (Button) clickEvent.getSource();
                try {
                    String selectedScannedDocumentId = selectedScannedDocumentButton.getId();
                    Desktop desktop = Desktop.getDesktop();
                    try {
                        System.out.println("SELECTED: " + String.format("%s%s.pdf", TextRecognition.DATAFOLDER, selectedScannedDocumentId));
                        desktop.open(new File(String.format("%s%s.pdf", TextRecognition.DATAFOLDER, selectedScannedDocumentId)));

                    } catch (IOException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("ERROR");
                        alert.setContentText("Cannot open file. Please check if the file exists. ");
                        alert.show();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        //Open Window with content of selected ScannedDocument
        this.viewDocument.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent clickEvent) {
                Button selectedScannedDocumentButton = (Button) clickEvent.getSource();
                try {
                    Integer selectedScannedDocumentId = Integer.parseInt(selectedScannedDocumentButton.getId());
                    AppLocalDatabase appLocalDatabase = new AppLocalDatabase();
                    ScannedDocument selectedScannedDocument = appLocalDatabase.getScannedDocumentById(selectedScannedDocumentId);

                    if (selectedScannedDocument == null) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("ERROR");
                        alert.setContentText("Cannot open document. Please check if the document exists. ");
                        alert.show();
                        return;
                    }

                    Parent root;
                    try {
                        root = FXMLLoader.load(getClass().getResource("/fxml/ScannedDocumentContent.fxml"));
                        Stage scannedDocumentContentWindow = new Stage();
                        Scene scene = new Scene(root);
                        scannedDocumentContentWindow.setTitle(String.format("%d - %s", selectedScannedDocument.getId(), selectedScannedDocument.getTitle()));
                        scannedDocumentContentWindow.setScene(scene);
                        ScannedDocumentContentController.selectedScannedDocument = selectedScannedDocument;
                        scannedDocumentContentWindow.show();
                        ScannedDocumentContentController.scannedDocumentContentWindow = scannedDocumentContentWindow;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(String documentDate) {
        this.documentDate = documentDate;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public Button getViewDocument() {
        return viewDocument;
    }

    public void setViewDocument(Button viewDocument) {
        this.viewDocument = viewDocument;
    }

    public Button getPdfDocument() {
        return pdfDocument;
    }

    public void setPdfDocument(Button pdfDocument) {
        this.pdfDocument = pdfDocument;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.id;
        hash = 59 * hash + Objects.hashCode(this.title);
        hash = 59 * hash + Objects.hashCode(this.documentDate);
        hash = 59 * hash + Objects.hashCode(this.languageName);
        hash = 59 * hash + Objects.hashCode(this.viewDocument);
        hash = 59 * hash + Objects.hashCode(this.pdfDocument);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ViewScannedDocument other = (ViewScannedDocument) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.documentDate, other.documentDate)) {
            return false;
        }
        if (!Objects.equals(this.languageName, other.languageName)) {
            return false;
        }
        if (!Objects.equals(this.viewDocument, other.viewDocument)) {
            return false;
        }
        if (!Objects.equals(this.pdfDocument, other.pdfDocument)) {
            return false;
        }
        return true;
    }

}
