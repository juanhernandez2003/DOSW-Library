package edu.eci.dosw.core.util;

public class ValidationUtil {
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}