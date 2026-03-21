package edu.eci.dosw.core.util;

import java.util.UUID;

public class IdGeneratorUtil {
    public static String generate() {
        return UUID.randomUUID().toString();
    }
}