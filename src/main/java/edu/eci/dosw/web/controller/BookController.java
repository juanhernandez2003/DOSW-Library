package edu.eci.dosw.controller;

import edu.eci.dosw.controller.dto.request.BookRequest;
import edu.eci.dosw.controller.dto.response.BookResponse;
import edu.eci.dosw.core.service.BookService;
import edu.eci.dosw.persistence.mapper.BookMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Gestión de libros e inventario")
public class BookController {

    private final BookService bookService;
    private final BookMapper bookMapper;

    @GetMapping
    @Operation(summary = "Consultar todos los libros")
    public ResponseEntity<List<BookResponse>> findAll() {
        return ResponseEntity.ok(bookMapper.toResponseList(bookService.findAll()));
    }

    @GetMapping("/available")
    @Operation(summary = "Consultar libros disponibles")
    public ResponseEntity<List<BookResponse>> findAvailable() {
        return ResponseEntity.ok(bookMapper.toResponseList(bookService.findAvailable()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un libro por id")
    public ResponseEntity<BookResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(bookMapper.toResponse(bookService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    @Operation(summary = "Crear un libro")
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookMapper.toResponse(bookService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    @Operation(summary = "Actualizar datos y stock de un libro")
    public ResponseEntity<BookResponse> update(@PathVariable Long id, @Valid @RequestBody BookRequest request) {
        return ResponseEntity.ok(bookMapper.toResponse(bookService.update(id, request)));
    }
}
