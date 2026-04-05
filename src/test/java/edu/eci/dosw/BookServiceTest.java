package edu.eci.dosw;

import edu.eci.dosw.controller.dto.request.BookRequest;
import edu.eci.dosw.core.exception.BusinessRuleException;
import edu.eci.dosw.core.model.BookAvailabilityStatus;
import edu.eci.dosw.core.model.PublicationType;
import edu.eci.dosw.core.service.BookService;
import edu.eci.dosw.infrastructure.mongodb.DualPersistenceSyncService;
import edu.eci.dosw.persistence.entity.Book;
import edu.eci.dosw.persistence.repository.BookRepository;
import java.time.LocalDate;
import java.util.Set;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class BookServiceTest {

    private BookRepository bookRepository;
    private DualPersistenceSyncService dualPersistenceSyncService;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookRepository = Mockito.mock(BookRepository.class);
        dualPersistenceSyncService = Mockito.mock(DualPersistenceSyncService.class);
        bookService = new BookService(bookRepository, dualPersistenceSyncService);
    }

    @Test
    void shouldRejectBookWithInvalidTotalCopies() {
        BookRequest request = new BookRequest("Clean Code", "Robert C. Martin", Set.of("Software"), PublicationType.BOOK,
                LocalDate.now().minusYears(1), "9780132350884", 450, "EN", "Prentice Hall", 0, 0);
        assertThrows(BusinessRuleException.class, () -> bookService.create(request));
    }

    @Test
    void shouldRejectBookWhenAvailableExceedsTotal() {
        BookRequest request = new BookRequest("Clean Architecture", "Robert C. Martin", Set.of("Software"), PublicationType.BOOK,
                LocalDate.now().minusYears(1), "9780134494166", 430, "EN", "Pearson", 3, 4);
        assertThrows(BusinessRuleException.class, () -> bookService.create(request));
    }

    @Test
    void shouldUpdateBookStock() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("DDD");
        book.setAuthor("Evans");
        book.setCategories(Set.of("Software"));
        book.setPublicationType(PublicationType.BOOK);
        book.setPublicationDate(LocalDate.now().minusYears(1));
        book.setIsbn("9780321125217");
        book.setPages(500);
        book.setLanguage("EN");
        book.setPublisherCompany("Addison-Wesley");
        book.setAvailabilityStatus(BookAvailabilityStatus.AVAILABLE);
        book.setTotalCopies(5);
        book.setAvailableCopies(2);
        book.setBorrowedCopies(3);
        book.setAddedToCatalogAt(LocalDate.now());

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Book updated = bookService.changeStock(1L, 8, 3);

        assertEquals(8, updated.getTotalCopies());
        assertEquals(3, updated.getAvailableCopies());
    }
}
