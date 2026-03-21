package edu.eci.dosw.core.service;

import edu.eci.dosw.core.exception.BookNotAvailableException;
import edu.eci.dosw.core.model.Book;
import edu.eci.dosw.core.util.IdGeneratorUtil;
import edu.eci.dosw.core.validator.BookValidator;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class BookService {
    private final Map<Book, Integer> books = new HashMap<>();
    private final BookValidator bookValidator;

    public BookService(BookValidator bookValidator) {
        this.bookValidator = bookValidator;
    }

    public Book addBook(String title, String author, int copies) {
        bookValidator.validate(title, author, copies);
        Book book = new Book(IdGeneratorUtil.generate(), title, author, copies);
        books.put(book, copies);
        return book;
    }

    public Map<Book, Integer> getAllBooks() {
        return books;
    }

    public Book getBookById(String id) {
        return books.keySet().stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new BookNotAvailableException(id));
    }

    public void updateAvailability(String id, boolean available) {
        Book book = getBookById(id);
        book.setAvailable(available);
    }

    public void decreaseCopy(String id) {
        Book book = getBookById(id);
        int copies = books.get(book);
        if (copies <= 0) throw new BookNotAvailableException(id);
        books.put(book, copies - 1);
        if (copies - 1 == 0) book.setAvailable(false);
    }

    public void increaseCopy(String id) {
        Book book = getBookById(id);
        int copies = books.get(book);
        books.put(book, copies + 1);
        book.setAvailable(true);
    }
}