// Book.java
package com.example.onlinebookreader;

import java.io.Serializable;

public class Book implements Serializable {
    private String title;
    private String author;
    private String category;
    private String description;
    private String link;
    private String coverImageUrl; // Add this field

    // Empty constructor required for Firestore
    public Book() {}

    public Book(String title, String author, String category, String description, String link, String coverImageUrl) {
        this.title = title;
        this.author = author;
        this.category = category;
        this.description = description;
        this.link = link;
        this.coverImageUrl = coverImageUrl; // Initialize the field
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }
}
