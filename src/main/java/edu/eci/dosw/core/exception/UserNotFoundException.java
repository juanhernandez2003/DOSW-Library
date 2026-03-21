package edu.eci.dosw.core.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String userId) {
        super("Usuario no encontrado con id: " + userId);
    }
}