package edu.eci.dosw.controller;

import java.util.ArrayList;
import java.util.List;
import edu.eci.dosw.controller.dto.BookDTO;
import edu.eci.dosw.core.model.Book;
import edu.eci.dosw.core.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Operaciones sobre libros")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Agregar un libro")
    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody BookDTO dto) {
        return ResponseEntity.ok(bookService.addBook(dto.getTitle(), dto.getAuthor(), dto.getCopies()));
    }

    @Operation(summary = "Obtener todos los libros")
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(new ArrayList<>(bookService.getAllBooks().keySet()));
    }

    @Operation(summary = "Obtener un libro por ID")
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable String id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @Operation(summary = "Actualizar disponibilidad de un libro")
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateAvailability(@PathVariable String id,
                                                     @RequestParam boolean available) {
        bookService.updateAvailability(id, available);
        return ResponseEntity.ok("Disponibilidad actualizada");
    }
}