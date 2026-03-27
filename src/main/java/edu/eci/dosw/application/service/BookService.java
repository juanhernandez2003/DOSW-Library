package edu.eci.dosw.core.service;

import edu.eci.dosw.controller.dto.request.BookRequest;
import edu.eci.dosw.core.exception.BusinessRuleException;
import edu.eci.dosw.core.exception.ResourceNotFoundException;
import edu.eci.dosw.persistence.entity.Book;
import edu.eci.dosw.persistence.repository.BookRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    @Transactional
    public Book create(BookRequest request) {
        validateStock(request.totalCopies(), request.availableCopies());
        Book book = new Book();
        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setTotalCopies(request.totalCopies());
        book.setAvailableCopies(request.availableCopies());
        return bookRepository.save(book);
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public List<Book> findAvailable() {
        return bookRepository.findByAvailableCopiesGreaterThan(0);
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + id));
    }

    @Transactional
    public Book update(Long id, BookRequest request) {
        validateStock(request.totalCopies(), request.availableCopies());
        Book book = findById(id);
        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setTotalCopies(request.totalCopies());
        book.setAvailableCopies(request.availableCopies());
        return bookRepository.save(book);
    }

    @Transactional
    public Book changeStock(Long id, Integer totalCopies, Integer availableCopies) {
        validateStock(totalCopies, availableCopies);
        Book book = findById(id);
        book.setTotalCopies(totalCopies);
        book.setAvailableCopies(availableCopies);
        return bookRepository.save(book);
    }

    void validateStock(Integer totalCopies, Integer availableCopies) {
        if (totalCopies == null || totalCopies <= 0) {
            throw new BusinessRuleException("La cantidad total de ejemplares debe ser mayor a 0");
        }
        if (availableCopies == null || availableCopies < 0) {
            throw new BusinessRuleException("La cantidad de ejemplares disponibles no puede ser menor a 0");
        }
        if (availableCopies > totalCopies) {
            throw new BusinessRuleException("Los ejemplares disponibles no pueden superar el stock total");
        }
    }
}
