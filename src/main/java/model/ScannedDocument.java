/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Date;
import java.util.Objects;

/**
 * Scanned Document: Content and Information
 *
 * @author A.Dridi
 */
public class ScannedDocument {

    private int id;
    private String title;
    private String content;
    private Date documentDate;
    private String languageName;

    public ScannedDocument() {

    }

    public ScannedDocument(int id, String title, String content, Date documentDate, String languageName) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.documentDate = documentDate;
        this.languageName = languageName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(Date documentDate) {
        this.documentDate = documentDate;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
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
        hash = 17 * hash + this.id;
        hash = 17 * hash + Objects.hashCode(this.title);
        hash = 17 * hash + Objects.hashCode(this.content);
        hash = 17 * hash + Objects.hashCode(this.documentDate);
        hash = 17 * hash + Objects.hashCode(this.languageName);
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
        final ScannedDocument other = (ScannedDocument) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.content, other.content)) {
            return false;
        }
        if (!Objects.equals(this.languageName, other.languageName)) {
            return false;
        }
        if (!Objects.equals(this.documentDate, other.documentDate)) {
            return false;
        }
        return true;
    }

}
