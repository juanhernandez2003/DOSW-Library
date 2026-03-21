package edu.eci.dosw.core.exception;

public class LoanLimitExceededException extends RuntimeException {
    public LoanLimitExceededException(String userId) {
        super("El usuario " + userId + " ha excedido el límite de préstamos");
    }
}