package edu.eci.dosw.core.service;

import edu.eci.dosw.core.exception.BookNotAvailableException;
import edu.eci.dosw.core.model.Book;
import edu.eci.dosw.core.model.Loan;
import edu.eci.dosw.core.model.User;
import edu.eci.dosw.core.util.DateUtil;
import edu.eci.dosw.core.util.IdGeneratorUtil;
import edu.eci.dosw.core.validator.LoanValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LoanService {
    private static final int MAX_LOANS_PER_USER = 3;
    private final List<Loan> loans = new ArrayList<>();
    private final BookService bookService;
    private final UserService userService;
    private final LoanValidator loanValidator;

    public LoanService(BookService bookService, UserService userService, LoanValidator loanValidator) {
        this.bookService = bookService;
        this.userService = userService;
        this.loanValidator = loanValidator;
    }

    public Loan createLoan(String bookId, String userId) {
        loanValidator.validate(bookId, userId);
        Book book = bookService.getBookById(bookId);
        User user = userService.getUserById(userId);

        if (!book.isAvailable()) throw new BookNotAvailableException(bookId);

        long activeLoans = loans.stream()
                .filter(l -> l.getUser().getId().equals(userId))
                .filter(l -> l.getStatus() == Loan.Status.ACTIVE)
                .count();
        if (activeLoans >= MAX_LOANS_PER_USER)
            throw new RuntimeException("Usuario ha excedido el límite de préstamos");

        bookService.decreaseCopy(bookId);
        Loan loan = new Loan(IdGeneratorUtil.generate(), book, user, DateUtil.today());
        loans.add(loan);
        return loan;
    }

    public List<Loan> getAllLoans() {
        return loans;
    }
}