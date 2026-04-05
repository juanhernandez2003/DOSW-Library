package edu.eci.dosw.controller.dto.request;

import edu.eci.dosw.core.model.PublicationType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

public record BookRequest(
        @NotBlank(message = "El titulo es obligatorio")
        String title,
        @NotBlank(message = "El autor es obligatorio")
        String author,
        @NotEmpty(message = "Debe indicar al menos una categoria")
        Set<@NotBlank(message = "La categoria no puede estar vacia") String> categories,
        @NotNull(message = "El tipo de publicacion es obligatorio")
        PublicationType publicationType,
        @NotNull(message = "La fecha de publicacion es obligatoria")
        @PastOrPresent(message = "La fecha de publicacion no puede ser futura")
        LocalDate publicationDate,
        @NotBlank(message = "El ISBN es obligatorio")
        @Size(min = 10, max = 20, message = "El ISBN debe tener entre 10 y 20 caracteres")
        String isbn,
        @NotNull(message = "La cantidad de paginas es obligatoria")
        @Min(value = 1, message = "La cantidad de paginas debe ser mayor a 0")
        Integer pages,
        @NotBlank(message = "El idioma es obligatorio")
        String language,
        @NotBlank(message = "La empresa publicadora es obligatoria")
        String publisherCompany,
        @NotNull(message = "El stock total es obligatorio")
        @Min(value = 1, message = "El stock total debe ser mayor a 0")
        Integer totalCopies,
        @NotNull(message = "El stock disponible es obligatorio")
        @Min(value = 0, message = "El stock disponible no puede ser negativo")
        Integer availableCopies
) {
}
