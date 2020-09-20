/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.adridi.scannedtextrecognitioncategorisation;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import db.AppLocalDatabase;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import model.OCRLanguage;
import model.ScannedDocument;
import model.ViewScannedDocument;
import services.TextRecognition;

/**
 * Main Window. Table with scanned text information. Scan text file/s buttons.
 *
 * @author A.Dridi
 */
public class MainWindowController implements Initializable {

    @FXML
    private ComboBox scannedImgLanguageComboBox = new ComboBox();
    @FXML
    private Label languageOfScannedImageLabel = new Label();
    @FXML
    private Label applicationTitle = new Label();
    @FXML
    private Button openFileButton = new Button();
    @FXML
    private Button openFilesButton = new Button();
    @FXML
    private TableView scannedDocumentsTable = new TableView();
    @FXML
    private Label copyrightFooterLabel = new Label();

    private TableColumn scannedDocumentsTableId;
    private TableColumn scannedDocumentsTableDate;
    private TableColumn scannedDocumentsTableTitle;
    private TableColumn scannedDocumentsTableLanguage;
    private TableColumn scannedDocumentsTableViewButton;
    private TableColumn scannedDocumentsTablePdfButton;

    public String selectedLanguageTesseractFile;
    //All languages that can be used for text recognition 
    public OCRLanguage languagesScannedImg = new OCRLanguage();
    public TextRecognition textRecognition = new TextRecognition();

    private File scannedImage;
    private List<File> scannedImageList;

    @FXML
    private BorderPane mainWindowView;

    private AppLocalDatabase localDatabase = new AppLocalDatabase();
    private List<ScannedDocument> scannedDocumentList;
    private ObservableList<ViewScannedDocument> viewScannedDocumentData;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.localDatabase.loadDatabase();
        this.scannedImgLanguageComboBox.getItems().addAll(languagesScannedImg.getAllLanguageList());
        this.scannedImgLanguageComboBox.getSelectionModel().select(0);
        this.selectedLanguageTesseractFile = languagesScannedImg.getTrainingLanguageFileByLanguageName(this.scannedImgLanguageComboBox.getSelectionModel().getSelectedItem().toString());
        loadComponentsListener();
        loadScannedDocumentTable();
        loadUiText();
    }

    public void loadUiText() {
        this.languageOfScannedImageLabel.setText("Language of scanned document: ");
        this.applicationTitle.setText("Scanned Text Recognition Categorization");
        this.openFileButton.setText("Scan File");
        this.openFilesButton.setText("Scan Files");
        this.copyrightFooterLabel.setText("Developed by a-dridi - http://github.com/a-dridi");
        this.scannedDocumentsTableId.setText("ID");
        this.scannedDocumentsTableDate.setText("Date");
        this.scannedDocumentsTableTitle.setText("Title");
        this.scannedDocumentsTableLanguage.setText("Language");
        this.scannedDocumentsTableViewButton.setText("View");
        this.scannedDocumentsTablePdfButton.setText("PDF");
    }

    public void loadComponentsListener() {
        this.scannedImgLanguageComboBox.valueProperty().addListener(new ChangeListener<OCRLanguage>() {
            @Override
            public void changed(ObservableValue<? extends OCRLanguage> ov, OCRLanguage t, OCRLanguage newSelectedLanguage) {
                if (!newSelectedLanguage.getLanguageName().trim().equals("")) {
                    selectedLanguageTesseractFile = languagesScannedImg.getTrainingLanguageFileByLanguageName(newSelectedLanguage.getLanguageName());
                }
            }
        });
    }

    public void loadScannedDocumentTable() {
        this.scannedDocumentsTableId = new TableColumn();
        this.scannedDocumentsTableDate = new TableColumn();
        this.scannedDocumentsTableTitle = new TableColumn();
        this.scannedDocumentsTableLanguage = new TableColumn();
        this.scannedDocumentsTableViewButton = new TableColumn<ViewScannedDocument, Object>();
        this.scannedDocumentsTablePdfButton = new TableColumn<ViewScannedDocument, Object>();

        this.scannedDocumentList = this.localDatabase.getAllScannedDocument();
        this.scannedDocumentsTableId.setCellValueFactory(new PropertyValueFactory<>("id"));
        this.scannedDocumentsTableDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        this.scannedDocumentsTableTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        this.scannedDocumentsTableLanguage.setCellValueFactory(new PropertyValueFactory<>("language"));
        this.scannedDocumentsTableViewButton.setCellValueFactory(new PropertyValueFactory<>("view"));
        this.scannedDocumentsTablePdfButton.setCellValueFactory(new PropertyValueFactory<>("pdf"));

        this.scannedDocumentsTable.setPlaceholder(new Label("No (scanned) documents added"));
        loadScannedDocuments();
        this.scannedDocumentsTable.getColumns().addAll(this.scannedDocumentsTableId, this.scannedDocumentsTableDate, this.scannedDocumentsTableTitle, this.scannedDocumentsTableLanguage, this.scannedDocumentsTableViewButton, this.scannedDocumentsTablePdfButton);
    }

    public void loadScannedDocuments() {
        this.viewScannedDocumentData = FXCollections.observableArrayList();

        try {
            ViewScannedDocument viewScannedDocument = new ViewScannedDocument();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            for (ScannedDocument scannedDocument : this.scannedDocumentList) {
                viewScannedDocument = new ViewScannedDocument(scannedDocument.getId(), scannedDocument.getTitle(), simpleDateFormat.format(scannedDocument.getDocumentDate()), scannedDocument.getLanguageName(), "View", "PDF");
                viewScannedDocument.setId(scannedDocument.getId());
                viewScannedDocument.setDocumentDate(simpleDateFormat.format(scannedDocument.getDocumentDate()));
                viewScannedDocument.setLanguageName(scannedDocument.getLanguageName());
                this.viewScannedDocumentData.add(viewScannedDocument);
            }
            this.scannedDocumentsTable.setItems(this.viewScannedDocumentData);

        } catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText("Cannot load documents. ");
            alert.show();
        }
    }

    /**
     * Open one image file that will be used to recognize the text. Creates text
     * recognition and saves it in database.
     */
    @FXML
    public void handleSelectOneImageFile(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JPEG file (*.jpg)", "*.jpg");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setSelectedExtensionFilter(new ExtensionFilter("JPEG File", "*.jpg"));
        fileChooser.setTitle("Select One Image File");

        try {
            this.scannedImage = fileChooser.showOpenDialog((Stage) this.mainWindowView.getScene().getWindow());
            if (this.scannedImage == null) {
                throw new NullPointerException();
            }
        } catch (NullPointerException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText("Cannot open image");
            alert.show();
            return;
        }
        //Recognize and save only one image
        recognizeAndSaveScannedText(false);
    }

    /**
     * Open multiple image files that will be used to recognize the text.
     * Creates text recognition and saves it in database.
     */
    @FXML
    public void handleSelectMultipleImageFile(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JPEG file (*.jpg)", "*.jpg");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setSelectedExtensionFilter(new ExtensionFilter("JPEG File", "*.jpg"));
        fileChooser.setTitle("Select Multiple Image Files");

        try {
            this.scannedImageList = fileChooser.showOpenMultipleDialog((Stage) this.mainWindowView.getScene().getWindow());
            if (this.scannedImageList == null) {
                throw new NullPointerException();
            }
        } catch (NullPointerException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText("Cannot open images");
            alert.show();
            return;
        }
        //Recognize and save multiple images
        recognizeAndSaveScannedText(true);
    }

    /**
     * Recognize opened image.
     *
     * @param recognizeMultipleImages true if a list of opened images should be
     * recognized.
     */
    public void recognizeAndSaveScannedText(boolean recognizeMultipleImages) {
        if (recognizeMultipleImages) {
            boolean errorMessageShowed = false;
            boolean error2MessageShowed = false;
            String recognizedText;
            String recognizedTitle;
            String recognizedDate;
            for (File openedImageFile : this.scannedImageList) {
                if (!(openedImageFile.getPath()).contains("jpg") && (!errorMessageShowed)) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("ERROR");
                    alert.setContentText("One file (at least) was not a jpg file. So it could not be opened!");
                    alert.show();
                    errorMessageShowed = true;
                }
                recognizedText = this.textRecognition.getStringTextFromScannedImage(openedImageFile.getAbsolutePath(), selectedLanguageTesseractFile);

                if (recognizedText == null || recognizedText.equals("") && (!error2MessageShowed)) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("ERROR");
                    alert.setContentText("One scanned document (at least or more) could not be recognized! Please check if the image has readable text.");
                    alert.show();
                    error2MessageShowed = true;
                }
                recognizedTitle = this.textRecognition.createTitleFromRecognizedText(recognizedText);
                recognizedDate = this.textRecognition.getDateFromFromRecognizedText(recognizedText);

                this.localDatabase.addScannedDocument(recognizedTitle, recognizedText, this.scannedImgLanguageComboBox.getSelectionModel().getSelectedItem().toString(), recognizedDate);
                createScannedDocumentPdf(this.localDatabase.getLatestScannedDocumentId() + 1, recognizedText);
                loadScannedDocuments();
            }
        } else {
            if (!(this.scannedImage.getPath()).contains("jpg")) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setContentText("Please select a jpg file!");
                alert.show();
            } else {
                try {
                    String recognizedText = this.textRecognition.getStringTextFromScannedImage(this.scannedImage.getAbsolutePath(), selectedLanguageTesseractFile);
                    if (recognizedText == null || recognizedText.equals("")) {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle("ERROR");
                        alert.setContentText("Your scanned document could not be recognized! Please check if the image has readable text.");
                        alert.show();
                        return;
                    }
                    String recognizedTitle = this.textRecognition.createTitleFromRecognizedText(recognizedText);
                    String recognizedDate = this.textRecognition.getDateFromFromRecognizedText(recognizedTitle);

                    this.localDatabase.addScannedDocument(recognizedTitle, recognizedText, this.languagesScannedImg.getLanguageCodeByLanguageName(this.scannedImgLanguageComboBox.getSelectionModel().getSelectedItem().toString()), recognizedDate);
                } catch (Error e) {
                    StringWriter errorStackTraceString = new StringWriter();
                    e.printStackTrace(new PrintWriter(errorStackTraceString));
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Tesseract ERROR");
                    errorStackTraceString.toString();
                    if (this.textRecognition.occuredExeption != null) {
                        errorStackTraceString = new StringWriter();
                        this.textRecognition.occuredExeption.printStackTrace(new PrintWriter(errorStackTraceString));
                        alert.setContentText("Please check also if you did add the required tesseract training files to the folder:  ".concat(TextRecognition.TRAININGDATAFOLDER).concat(" - ").concat(errorStackTraceString.toString().substring(0, 300)));
                    } else {
                        alert.setContentText("Please check also if you did add the required tesseract training files to the folder:  ".concat(TextRecognition.TRAININGDATAFOLDER).concat(" - ").concat(errorStackTraceString.toString().substring(0, 300)));
                    }
                    alert.show();
                    System.out.println(errorStackTraceString.toString());
                }
            }
        }
    }

    /**
     * Create a pdf file with the content of the String variable "content"
     *
     * @param id
     * @param content
     * @return the relative path of the created pdf. Null if pdf could not be
     * created.
     */
    public String createScannedDocumentPdf(Integer id, String content) {
        Document document = new Document();
        try {
            String pdfFilePath = String.format("%s/%s.pdf", TextRecognition.DATAFOLDER, id);
            PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
            PdfPTable scannedDocumentContent = new PdfPTable(1);
            scannedDocumentContent.setWidthPercentage(100);
            scannedDocumentContent.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            scannedDocumentContent.getDefaultCell().setVerticalAlignment(Element.ALIGN_LEFT);
            String[] contentLines = content.split("\n");
            for (String contentLine : contentLines) {
                scannedDocumentContent.addCell((new Phrase(new Chunk(contentLine))));
            }

            document.add(scannedDocumentContent);
            document.close();
            return pdfFilePath;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            showPdfCreationError();
            return null;
        } catch (DocumentException e) {
            showPdfCreationError();
            e.printStackTrace();
            return null;
        }
    }

    public void showPdfCreationError() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setContentText("Cannot create pdf! Please check if the folder \"" + TextRecognition.DATAFOLDER + "\" exists.");
        alert.show();
    }

    public ComboBox getScannedImgLanguageComboBox() {
        return scannedImgLanguageComboBox;
    }

    public void setScannedImgLanguageComboBox(ComboBox scannedImgLanguageComboBox) {
        this.scannedImgLanguageComboBox = scannedImgLanguageComboBox;
    }

    public Label getLanguageOfScannedImageLabel() {
        return languageOfScannedImageLabel;
    }

    public void setLanguageOfScannedImageLabel(Label languageOfScannedImageLabel) {
        this.languageOfScannedImageLabel = languageOfScannedImageLabel;
    }

    public Label getApplicationTitle() {
        return applicationTitle;
    }

    public void setApplicationTitle(Label applicationTitle) {
        this.applicationTitle = applicationTitle;
    }

    public Button getOpenFileButton() {
        return openFileButton;
    }

    public void setOpenFileButton(Button openFileButton) {
        this.openFileButton = openFileButton;
    }

    public Button getOpenFilesButton() {
        return openFilesButton;
    }

    public void setOpenFilesButton(Button openFilesButton) {
        this.openFilesButton = openFilesButton;
    }

    public TableView getScannedDocumentsTable() {
        return scannedDocumentsTable;
    }

    public void setScannedDocumentsTable(TableView scannedDocumentsTable) {
        this.scannedDocumentsTable = scannedDocumentsTable;
    }

    public Label getCopyrightFooterLabel() {
        return copyrightFooterLabel;
    }

    public void setCopyrightFooterLabel(Label copyrightFooterLabel) {
        this.copyrightFooterLabel = copyrightFooterLabel;
    }

    public BorderPane getMainWindowView() {
        return mainWindowView;
    }

    public void setMainWindowView(BorderPane mainWindowView) {
        this.mainWindowView = mainWindowView;
    }

}
