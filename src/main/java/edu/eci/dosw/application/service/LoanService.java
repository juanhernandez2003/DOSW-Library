package edu.eci.dosw.core.service;

import edu.eci.dosw.core.model.BookAvailabilityStatus;
import edu.eci.dosw.core.exception.BusinessRuleException;
import edu.eci.dosw.core.exception.ForbiddenOperationException;
import edu.eci.dosw.core.exception.ResourceNotFoundException;
import edu.eci.dosw.core.model.LoanStatus;
import edu.eci.dosw.persistence.entity.LoanHistoryEntry;
import edu.eci.dosw.core.model.Role;
import edu.eci.dosw.infrastructure.mongodb.DualPersistenceSyncService;
import edu.eci.dosw.persistence.entity.Book;
import edu.eci.dosw.persistence.entity.LibraryUser;
import edu.eci.dosw.persistence.entity.Loan;
import edu.eci.dosw.persistence.repository.BookRepository;
import edu.eci.dosw.persistence.repository.LoanRepository;
import edu.eci.dosw.persistence.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoanService {

    private static final int MAX_ACTIVE_LOANS_PER_USER = 3;

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final DualPersistenceSyncService dualPersistenceSyncService;

    @Transactional
    public Loan createLoan(Long userId, Long bookId) {
        LibraryUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + userId));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id: " + bookId));

        if (book.getAvailableCopies() <= 0) {
            throw new BusinessRuleException("El libro no tiene unidades disponibles para préstamo");
        }

        long activeLoans = loanRepository.countByUserAndStatus(user, LoanStatus.ACTIVE);
        if (activeLoans >= MAX_ACTIVE_LOANS_PER_USER) {
            throw new BusinessRuleException("El usuario ha excedido el límite de préstamos activos");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        book.setBorrowedCopies(book.getBorrowedCopies() + 1);
        book.setAvailabilityStatus(resolveAvailabilityStatus(book.getAvailableCopies(), book.getTotalCopies()));
        bookRepository.save(book);

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setUser(user);
        loan.setLoanDate(LocalDate.now());
        loan.setStatus(LoanStatus.ACTIVE);
        loan.getHistory().add(new LoanHistoryEntry(LoanStatus.ACTIVE, LocalDateTime.now()));
        Loan savedLoan = loanRepository.save(loan);
        dualPersistenceSyncService.syncBook(book);
        dualPersistenceSyncService.syncLoan(savedLoan);
        return savedLoan;
    }

    @Transactional
    public Loan returnLoan(Long loanId, Long requesterId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado con id: " + loanId));
        LibraryUser requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + requesterId));

        boolean isOwner = loan.getUser().getId().equals(requesterId);
        boolean isLibrarian = requester.getRole() == Role.LIBRARIAN;
        if (!isOwner && !isLibrarian) {
            throw new ForbiddenOperationException("No puede devolver préstamos de otros usuarios");
        }

        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new BusinessRuleException("No se puede devolver un préstamo que ya fue devuelto");
        }

        Book book = loan.getBook();
        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new BusinessRuleException("El stock disponible ya alcanzó el máximo configurado");
        }

        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnedDate(LocalDate.now());
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        book.setBorrowedCopies(book.getBorrowedCopies() - 1);
        book.setAvailabilityStatus(resolveAvailabilityStatus(book.getAvailableCopies(), book.getTotalCopies()));
        loan.getHistory().add(new LoanHistoryEntry(LoanStatus.RETURNED, LocalDateTime.now()));

        bookRepository.save(book);
        Loan savedLoan = loanRepository.save(loan);
        dualPersistenceSyncService.syncBook(book);
        dualPersistenceSyncService.syncLoan(savedLoan);
        return savedLoan;
    }

    public List<Loan> findAll() {
        return loanRepository.findAll();
    }

    public List<Loan> findByUser(Long userId) {
        LibraryUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + userId));
        return loanRepository.findByUserOrderByLoanDateDesc(user);
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
