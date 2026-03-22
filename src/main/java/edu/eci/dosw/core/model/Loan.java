package edu.eci.dosw.core.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Loan {
    public enum Status { ACTIVE, RETURNED }

    private String id;
    private Book book;
    private User user;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private Status status;

    public Loan(String id, Book book, User user, LocalDate loanDate) {
        this.id = id;
        this.book = book;
        this.user = user;
        this.loanDate = loanDate;
        this.status = Status.ACTIVE;
    }
}