package edu.eci.dosw.controller;

import edu.eci.dosw.controller.dto.BookDTO;
import edu.eci.dosw.core.model.Book;
import edu.eci.dosw.core.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody BookDTO dto) {
        return ResponseEntity.ok(bookService.addBook(dto.getTitle(), dto.getAuthor(), dto.getCopies()));
    }

    @GetMapping
    public ResponseEntity<Map<Book, Integer>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable String id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateAvailability(@PathVariable String id,
                                                     @RequestParam boolean available) {
        bookService.updateAvailability(id, available);
        return ResponseEntity.ok("Disponibilidad actualizada");
    }
}