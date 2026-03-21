package edu.eci.dosw.core.model;

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

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public int getCopies() { return copies; }
    public void setCopies(int copies) { this.copies = copies; }
}