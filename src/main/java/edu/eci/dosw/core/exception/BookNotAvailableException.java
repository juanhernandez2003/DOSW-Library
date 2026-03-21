package edu.eci.dosw.core.exception;

public class BookNotAvailableException extends RuntimeException {
    public BookNotAvailableException(String bookId) {
        super("El libro con id " + bookId + " no está disponible");
    }
}