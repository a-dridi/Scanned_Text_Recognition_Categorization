/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.util.ArrayList;
import java.util.List;
import model.ScannedDocument;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;
import services.TextRecognition;

/**
 * SQLite database that saves documents and settings
 *
 * @author A.Dridi
 */
public class AppLocalDatabase {

    private String dbFilePath = "jdbc:sqlite:scannedtextrecognitioncategorization.db";
    private List<ScannedDocument> scannedDocuments = new ArrayList<>();
    private Connection connection;
    private Statement statement;
    private String errorMessage;

    /**
     * Load database and create statement. Run at the start of the application.
     * Database data can be accessed then through getter methods.
     *
     * @return
     */
    public boolean loadDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(this.dbFilePath);
            this.statement = connection.createStatement();

            try {
                ResultSet allScannedDocumentResultSet = this.statement.executeQuery("SELECT * FROM scanneddocument");

                if (allScannedDocumentResultSet.isClosed() || !allScannedDocumentResultSet.next() || allScannedDocumentResultSet.getString(1) == null) {
                    throw new SQLiteException("Database started for the first time", SQLiteErrorCode.SQLITE_ERROR);
                }
            } catch (SQLiteException e) {
                String createScannedDocumentTable = "CREATE TABLE IF NOT EXISTS scanneddocument " + "(id INTEGER PRIMARY KEY,"
                        + "title TEXT," + "content TEXT," + "documentDate TEXT," + "languageName TEXT" + ")";
                this.statement.execute(createScannedDocumentTable);
            }
            return true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Database cannot be created or accessed. " + e.toString());
            alert.show();
            e.printStackTrace();
            return false;
        } finally {
            try {
                this.statement.close();
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get all scanned documents.
     *
     * @return Null if SQL connection failed.
     */
    public List<ScannedDocument> getAllScannedDocument() {
        List<ScannedDocument> scannedDocumentList = new ArrayList<>();
        SimpleDateFormat dayMonthYearFormat = new SimpleDateFormat("dd.MM.yyyy");

        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(this.dbFilePath);
            this.statement = connection.createStatement();

            ResultSet allScannedDocumentResultSet = this.statement.executeQuery("SELECT * FROM scanneddocument");
            while (allScannedDocumentResultSet != null && allScannedDocumentResultSet.next()) {
                Date parsedDate = null;
                try {
                    parsedDate = dayMonthYearFormat.parse(allScannedDocumentResultSet.getString("documentDate"));
                } catch (ParseException e) {
                }
                scannedDocumentList.add(new ScannedDocument(allScannedDocumentResultSet.getInt("id"), allScannedDocumentResultSet.getString("title"), allScannedDocumentResultSet.getString("content"), parsedDate, allScannedDocumentResultSet.getString("languageName")));
            }
            //  allScannedDocumentResultSet.close();
            return scannedDocumentList;
        } catch (Exception e) {
            System.err.println("getAllScannedDocument - " + e.getClass().getName() + ": " + e.getMessage());
            this.errorMessage = "getAllScannedDocument: " + e.getMessage();
            errorCannotAccessDB(e.getMessage());
            return null;
        } finally {
            try {
                this.statement.close();
                this.connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public ScannedDocument getScannedDocumentById(Integer id) {
        ScannedDocument scannedDocument;
        SimpleDateFormat dayMonthYearFormat = new SimpleDateFormat("dd.MM.yyyy");

        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(this.dbFilePath);
            this.statement = connection.createStatement();

            ResultSet scannedDocumentByIdResultSet = this.statement.executeQuery("SELECT * FROM scanneddocument WHERE id = " + id);
            Date parsedDate = null;
            try {
                parsedDate = dayMonthYearFormat.parse(scannedDocumentByIdResultSet.getString("documentDate"));
            } catch (ParseException e) {
            }
            scannedDocument = new ScannedDocument(scannedDocumentByIdResultSet.getInt("id"), scannedDocumentByIdResultSet.getString("title"), scannedDocumentByIdResultSet.getString("content"), parsedDate, scannedDocumentByIdResultSet.getString("languageName"));
            return scannedDocument;
        } catch (Exception e) {
            System.err.println("getScannedDocumentById - " + e.getClass().getName() + ": " + e.getMessage());
            this.errorMessage = "getScannedDocumentById: " + e.getMessage();
            errorCannotAccessDB(e.getMessage());
            return null;
        } finally {
            try {
                this.statement.close();
                this.connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Save content and information about recognized scanned documents
     *
     * @param title
     * @param content
     * @param languageName
     * @param documentDate data format should be dd.MM.yyyy
     * @return false if saving failed
     */
    public boolean addScannedDocument(String title, String content, String languageName, String documentDate) {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(this.dbFilePath);
            this.statement = connection.createStatement();

            this.statement.executeUpdate("INSERT INTO scanneddocument (title, content, documentDate, languageName) VALUES ('" + title + "', '" + content
                    + "', '" + documentDate + "', '" + languageName + "')");
            return true;
        } catch (Exception e) {
            System.err.println("addScannedDocument - " + e.getClass().getName() + ": " + e.getMessage());
            this.errorMessage = "addScannedDocument: " + e.getMessage();
            errorCannotAccessDB(e.getMessage());
            return false;
        } finally {
            try {
                this.statement.close();
                this.connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean deleteScannedDocumentById(Integer id) {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(this.dbFilePath);
            this.statement = connection.createStatement();

            this.statement.executeQuery("DELETE FROM scanneddocument WHERE id = " + id);
            return true;
        } catch (Exception e) {
            System.err.println("deleteScannedDocumentById - " + e.getClass().getName() + ": " + e.getMessage());
            this.errorMessage = "deleteScannedDocumentById: " + e.getMessage();
            errorCannotAccessDB(e.getMessage());
            return false;
        } finally {
            try {
                this.statement.close();
                this.connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Get id of the latest scanned document in the database
     *
     * @return
     */
    public int getLatestScannedDocumentId() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(this.dbFilePath);
            this.statement = connection.createStatement();

            ResultSet allScannedDocumentResultSet = this.statement.executeQuery("SELECT id FROM scanneddocument ORDER BY id DESC LIMIT 1");
            return allScannedDocumentResultSet.getInt("id");
        } catch (Exception e) {
            System.err.println("getAllScannedDocument - " + e.getClass().getName() + ": " + e.getMessage());
            this.errorMessage = "getAllScannedDocument: " + e.getMessage();
            errorCannotAccessDB(e.getMessage());
            return 0;
        } finally {
            try {
                this.statement.close();
                this.connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                errorCannotAccessDB(ex.getMessage());
            }
        }
    }

    public void errorCannotAccessDB(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setContentText("Cannot access database file. Please check if it is not corrupt. " + errorMessage);
        alert.show();
    }

}
