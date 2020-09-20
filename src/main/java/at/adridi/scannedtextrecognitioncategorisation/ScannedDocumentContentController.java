/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.adridi.scannedtextrecognitioncategorisation;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.ScannedDocument;

/**
 * Display content of selected ScannedDocument
 *
 * @author A.Dridi
 */
public class ScannedDocumentContentController implements Initializable {

    public static ScannedDocument selectedScannedDocument;
    public static Stage scannedDocumentContentWindow;

    @FXML
    private TextArea scannedDocumentContent = new TextArea();
    
    @FXML
    private Button closeButton = new Button();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadUiText();
        if (this.selectedScannedDocument != null) {
            this.scannedDocumentContent.setText(selectedScannedDocument.getContent());
            this.scannedDocumentContent.setEditable(false);
        }
    }

    @FXML
    private void handleCloseWindow(ActionEvent actionEvent) {
        if (this.scannedDocumentContentWindow != null) {
            this.scannedDocumentContentWindow.close();
        }
    }
    
    public void loadUiText(){
        this.closeButton.setText("Close");
    }
}
