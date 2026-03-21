package edu.eci.dosw.core.util;

import java.time.LocalDate;

public class DateUtil {
    public static LocalDate today() {
        return LocalDate.now();
    }

    public static LocalDate returnDateFrom(LocalDate loanDate, int days) {
        return loanDate.plusDays(days);
    }
}