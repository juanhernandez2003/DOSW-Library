package edu.eci.dosw.controller.dto.response;

import edu.eci.dosw.core.model.BookAvailabilityStatus;
import edu.eci.dosw.core.model.PublicationType;
import java.time.LocalDate;
import java.util.Set;

public record BookResponse(
        Long id,
        String title,
        String author,
        Set<String> categories,
        PublicationType publicationType,
        LocalDate publicationDate,
        String isbn,
        Integer pages,
        String language,
        String publisherCompany,
        BookAvailabilityStatus availabilityStatus,
        Integer totalCopies,
        Integer availableCopies,
        Integer borrowedCopies,
        LocalDate addedToCatalogAt
) {
}
