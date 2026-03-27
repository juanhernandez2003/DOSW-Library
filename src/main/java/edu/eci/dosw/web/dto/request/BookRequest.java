package edu.eci.dosw.controller.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookRequest(
        @NotBlank(message = "El título es obligatorio")
        String title,
        @NotBlank(message = "El autor es obligatorio")
        String author,
        @NotNull(message = "El stock total es obligatorio")
        @Min(value = 1, message = "El stock total debe ser mayor a 0")
        Integer totalCopies,
        @NotNull(message = "El stock disponible es obligatorio")
        @Min(value = 0, message = "El stock disponible no puede ser negativo")
        Integer availableCopies
) {
}
