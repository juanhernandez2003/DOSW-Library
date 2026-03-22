package edu.eci.dosw.core.model;

import lombok.Data;

@Data
public class Book {
    private String id;
    private String title;
    private String author;
    private boolean available;
    private int copies;

    public Book(String id, String title, String author, int copies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.copies = copies;
        this.available = copies > 0;
    }
}