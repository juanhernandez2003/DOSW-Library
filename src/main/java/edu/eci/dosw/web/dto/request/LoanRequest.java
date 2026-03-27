package edu.eci.dosw.controller.dto.request;

import jakarta.validation.constraints.NotNull;

public record LoanRequest(
        @NotNull(message = "El id del libro es obligatorio")
        Long bookId
) {
}
