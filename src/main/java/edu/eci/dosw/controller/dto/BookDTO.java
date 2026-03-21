package edu.eci.dosw.controller.dto;

public class BookDTO {
    private String title;
    private String author;
    private int copies;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public int getCopies() { return copies; }
    public void setCopies(int copies) { this.copies = copies; }
}
