package edu.eci.dosw.infrastructure.mongodb.document;

import edu.eci.dosw.core.model.BookAvailabilityStatus;
import edu.eci.dosw.core.model.PublicationType;
import java.time.LocalDate;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "books_catalog")
public class MongoBookDocument {

    @Id
    private String id;

    private Long relationalId;
    private String title;
    private String author;
    private Set<String> categories;
    private PublicationType publicationType;
    private LocalDate publicationDate;
    private String isbn;
    private Integer pages;
    private String language;
    private String publisherCompany;
    private BookAvailabilityStatus availabilityStatus;
    private Integer totalCopies;
    private Integer availableCopies;
    private Integer borrowedCopies;
    private LocalDate addedToCatalogAt;
    private LocalDate synchronizedAt;
}
