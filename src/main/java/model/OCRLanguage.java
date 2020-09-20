/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 *
 * All languages that are available for text recognition
 *
 * @author A.Dridi
 */
public class OCRLanguage {

    private String languageCode;
    private String languageName;
    private String tesseractLanguageTrainingFile;
    private List<OCRLanguage> languagesList;

    public OCRLanguage() {

    }

    public OCRLanguage(String languageCode, String languageName, String tesseractLanguageTrainingFile) {
        this.languageCode = languageCode;
        this.languageName = languageName;
    }

    public List<OCRLanguage> getAllLanguageList() {
        this.languagesList = new ArrayList<>();
        this.languagesList.add(new OCRLanguage("en", "English", "eng.traineddata"));
        this.languagesList.add(new OCRLanguage("de", "Deutsch", "deu.traineddata"));
        this.languagesList.add(new OCRLanguage("fr", "Français", "fra.traineddata "));
        this.languagesList.add(new OCRLanguage("es", "Español", "spa.traineddata "));
        this.languagesList.add(new OCRLanguage("ja", "日本語", "jpn.traineddata "));
        this.languagesList.add(new OCRLanguage("ko", "한국어", "kor.traineddata "));
        return languagesList;
    }

    /**
     * Get tesseract language file name for language name (native language
     * name).
     *
     * @param languageName
     * @return Empty string, when language name does not exist
     */
    public String getTrainingLanguageFileByLanguageName(String languageName) {
        try {
            return this.languagesList.stream().filter(ocrLanguageFile -> ocrLanguageFile.languageName == languageName).findFirst().get().tesseractLanguageTrainingFile;
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    /**
     * Get language code for language name (native language name).
     *
     * @param languageName
     * @return Empty string, when language name does not exist
     */
    public String getLanguageCodeByLanguageName(String languageName) {
        try {
            return this.languagesList.stream().filter(ocrLanguageFile -> ocrLanguageFile.languageName == languageName).findFirst().get().languageCode;
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    public String getLanguageCode() {
        return this.languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageName() {
        return this.languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getTesseractLanguageTrainingFile() {
        return tesseractLanguageTrainingFile;
    }

    public void setTesseractLanguageTrainingFile(String tesseractLanguageTrainingFile) {
        this.tesseractLanguageTrainingFile = tesseractLanguageTrainingFile;
    }

    @Override
    public String toString() {
        return this.languageName;
    }

}
