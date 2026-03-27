package edu.eci.dosw.controller.dto.response;

public record BookResponse(
        Long id,
        String title,
        String author,
        Integer totalCopies,
        Integer availableCopies
) {
}
