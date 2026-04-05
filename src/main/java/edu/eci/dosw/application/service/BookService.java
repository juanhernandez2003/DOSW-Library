package edu.eci.dosw.core.service;

import edu.eci.dosw.controller.dto.request.BookRequest;
import edu.eci.dosw.core.model.BookAvailabilityStatus;
import edu.eci.dosw.core.exception.BusinessRuleException;
import edu.eci.dosw.core.exception.ResourceNotFoundException;
import edu.eci.dosw.infrastructure.mongodb.DualPersistenceSyncService;
import edu.eci.dosw.persistence.entity.Book;
import edu.eci.dosw.persistence.repository.BookRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final DualPersistenceSyncService dualPersistenceSyncService;

    @Transactional
    public Book create(BookRequest request) {
        validateStock(request.totalCopies(), request.availableCopies());
        validateIsbn(request.isbn(), null);
        Book book = new Book();
        applyBookData(book, request);
        book.setAddedToCatalogAt(LocalDate.now());
        Book savedBook = bookRepository.save(book);
        dualPersistenceSyncService.syncBook(savedBook);
        return savedBook;
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
        validateIsbn(request.isbn(), book.getIsbn());
        applyBookData(book, request);
        Book savedBook = bookRepository.save(book);
        dualPersistenceSyncService.syncBook(savedBook);
        return savedBook;
    }

    @Transactional
    public Book changeStock(Long id, Integer totalCopies, Integer availableCopies) {
        validateStock(totalCopies, availableCopies);
        Book book = findById(id);
        book.setTotalCopies(totalCopies);
        book.setAvailableCopies(availableCopies);
        book.setBorrowedCopies(totalCopies - availableCopies);
        book.setAvailabilityStatus(resolveAvailabilityStatus(availableCopies, totalCopies));
        Book savedBook = bookRepository.save(book);
        dualPersistenceSyncService.syncBook(savedBook);
        return savedBook;
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

    private void applyBookData(Book book, BookRequest request) {
        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setCategories(request.categories());
        book.setPublicationType(request.publicationType());
        book.setPublicationDate(request.publicationDate());
        book.setIsbn(request.isbn());
        book.setPages(request.pages());
        book.setLanguage(request.language());
        book.setPublisherCompany(request.publisherCompany());
        book.setTotalCopies(request.totalCopies());
        book.setAvailableCopies(request.availableCopies());
        book.setBorrowedCopies(request.totalCopies() - request.availableCopies());
        book.setAvailabilityStatus(resolveAvailabilityStatus(request.availableCopies(), request.totalCopies()));
    }

    private void validateIsbn(String isbn, String currentIsbn) {
        if (currentIsbn != null && currentIsbn.equals(isbn)) {
            return;
        }
        if (bookRepository.existsByIsbn(isbn)) {
            throw new BusinessRuleException("El ISBN ya existe");
        }
    }

    private BookAvailabilityStatus resolveAvailabilityStatus(Integer availableCopies, Integer totalCopies) {
        if (availableCopies == 0) {
            return BookAvailabilityStatus.OUT_OF_STOCK;
        }
        if (availableCopies <= Math.max(1, totalCopies / 4)) {
            return BookAvailabilityStatus.LOW_STOCK;
        }
        return BookAvailabilityStatus.AVAILABLE;
    }
}
