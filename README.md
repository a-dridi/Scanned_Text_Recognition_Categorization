# Scanned Text Recognition Categorization

A Java desktop application, which does text recognition on your scanned images. Recognized text will be saved as a PDF and in a local database. This application does also detect dates in your scanned document and it chooses a title for your recognized document.

![Screenshot of Application Scanned Text Recognition Categorization](https://raw.githubusercontent.com/a-dridi/Scanned_Text_Recognition_Categorization/master/screenshot.PNG)


# Requirements
- All dependencies in pom.xml file. 
- IMPORTANT: Tesseract train data files for the languages available in this app. The following files are needed: deu.traineddata, eng.traineddata, fra.traineddata, kor.traineddata, jpn.traineddata, spa.traineddata. These files must be added to the folder "textrecognition" and they can be downloaded from: https://github.com/tesseract-ocr/tessdata


# Installation
Open this project in an IDE (Netbeans recommended) and compile it. Check my other project "QuickCustomerManagement" for example on how to create an installer (in Windows) for jar files.
Jar file will be saved in the folder "target" after you did compile this project. 
The folder "textrecognition" must be always in the root folder of this project or application. 

# Available Languages
These are the available languages for text recognition:
English, Deutsch, Français, Español, 日本語, 한국어


## Authors

* **A. Dridi** - [a-dridi](https://github.com/a-dridi/)
