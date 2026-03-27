package edu.eci.dosw;

import edu.eci.dosw.controller.dto.request.BookRequest;
import edu.eci.dosw.core.exception.BusinessRuleException;
import edu.eci.dosw.core.service.BookService;
import edu.eci.dosw.persistence.entity.Book;
import edu.eci.dosw.persistence.repository.BookRepository;
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
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookRepository = Mockito.mock(BookRepository.class);
        bookService = new BookService(bookRepository);
    }

    @Test
    void shouldRejectBookWithInvalidTotalCopies() {
        BookRequest request = new BookRequest("Clean Code", "Robert C. Martin", 0, 0);
        assertThrows(BusinessRuleException.class, () -> bookService.create(request));
    }

    @Test
    void shouldRejectBookWhenAvailableExceedsTotal() {
        BookRequest request = new BookRequest("Clean Architecture", "Robert C. Martin", 3, 4);
        assertThrows(BusinessRuleException.class, () -> bookService.create(request));
    }

    @Test
    void shouldUpdateBookStock() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("DDD");
        book.setAuthor("Evans");
        book.setTotalCopies(5);
        book.setAvailableCopies(2);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Book updated = bookService.changeStock(1L, 8, 3);

        assertEquals(8, updated.getTotalCopies());
        assertEquals(3, updated.getAvailableCopies());
    }
}
