package edu.eci.dosw;

import edu.eci.dosw.core.exception.BookNotAvailableException;
import edu.eci.dosw.core.model.Book;
import edu.eci.dosw.core.service.BookService;
import edu.eci.dosw.core.validator.BookValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {

    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookService(new BookValidator());
    }

    @Test
    void testAddBookSuccess() {
        Book book = bookService.addBook("Clean Code", "Robert Martin", 3);
        assertNotNull(book.getId());
        assertEquals("Clean Code", book.getTitle());
        assertTrue(book.isAvailable());
    }

    @Test
    void testGetBookByIdNotFound() {
        assertThrows(BookNotAvailableException.class,
                () -> bookService.getBookById("id-inexistente"));
    }

    @Test
    void testUpdateAvailability() {
        Book book = bookService.addBook("Refactoring", "Fowler", 2);
        bookService.updateAvailability(book.getId(), false);
        assertFalse(bookService.getBookById(book.getId()).isAvailable());
    }

    @Test
    void testDecreaseCopyToZeroSetsUnavailable() {
        Book book = bookService.addBook("DDD", "Evans", 1);
        bookService.decreaseCopy(book.getId());
        assertFalse(bookService.getBookById(book.getId()).isAvailable());
    }

    @Test
    void testDecreaseCopyNoStock() {
        Book book = bookService.addBook("TDD", "Beck", 0);
        assertThrows(BookNotAvailableException.class,
                () -> bookService.decreaseCopy(book.getId()));
    }

    @Test
    void testAddBookEmptyTitleThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook("", "Autor", 1));
    }
}
