/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

/**
 * Do text recognition. Saves information about the train data folders.
 *
 * @author A.Dridi
 */
public class TextRecognition {

    public static String DATAFOLDER = "textrecognition";
    public static String TRAININGDATAFOLDER = "textrecognition";
    public TesseractException occuredExeption;
    
    /**
     * Does text recognition on a scanned image. Returns the recognized text as
     * a string object.
     *
     * @param scannedImageFullPath
     * @param trainDataFileName
     * @return null if recognition failed
     */
    public String getStringTextFromScannedImage(String scannedImageFullPath, String trainDataFileName) {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(DATAFOLDER);
        tesseract.setLanguage(trainDataFileName);

        try {
            String recognizedText = tesseract.doOCR(new File(scannedImageFullPath));
            return recognizedText;

        } catch (TesseractException ex) {
            this.occuredExeption = ex;
            ex.printStackTrace();
            return null;
        }

    }

    /**
     * Get title from recognized scanned text
     *
     * @param stringTextFromScannedImage text that was recognized from a scanned
     * image
     * @return
     */
    public String createTitleFromRecognizedText(String stringTextFromScannedImage) {
        String[] stringTextFromScannedImageArray = stringTextFromScannedImage.split("\n");
        String parsedTitle = "";
        int i = 0;
        checkTitle:
        for (String line : stringTextFromScannedImageArray) {
            if (i > 2 && !line.trim().equals("")) {
                parsedTitle = line.trim();
                break checkTitle;
            }
            i++;
        }
        return parsedTitle;
    }

    /**
     * Get date from recognized scanned text
     *
     * @param stringTextFromScannedImage text that was recognized from a scanned
     * image
     * @return The current date if date could not be parsed. Date in format:
     * dd.MM.yyyy
     */
    public String getDateFromFromRecognizedText(String stringTextFromScannedImage) {
        String regex = "(\\d{2}[./]\\d{2}[./]\\d{4})";
        SimpleDateFormat dateFormatPoints = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat dateFormatSlash = new SimpleDateFormat("dd/MM/yyyy");
        String parsedDateString = dateFormatPoints.format(new Date());
        Matcher matchDate = Pattern.compile(regex).matcher(stringTextFromScannedImage);
        if (matchDate.find()) {
            if (matchDate.group(1).contains(".")) {
                parsedDateString = matchDate.group(1);
            } else if (matchDate.group(1).contains("/")) {
                try {
                    parsedDateString = dateFormatPoints.format(dateFormatSlash.parse(matchDate.group(1)));
                } catch (ParseException ex) {
                    //parsedDateString will be the current date
                }
            }
        }
        return parsedDateString;
    }
}
