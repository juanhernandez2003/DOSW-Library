package edu.eci.dosw;

import edu.eci.dosw.core.exception.BookNotAvailableException;
import edu.eci.dosw.core.model.Loan;
import edu.eci.dosw.core.model.User;
import edu.eci.dosw.core.model.Book;
import edu.eci.dosw.core.service.BookService;
import edu.eci.dosw.core.service.LoanService;
import edu.eci.dosw.core.service.UserService;
import edu.eci.dosw.core.validator.BookValidator;
import edu.eci.dosw.core.validator.LoanValidator;
import edu.eci.dosw.core.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoanServiceTest {

    private LoanService loanService;
    private BookService bookService;
    private UserService userService;
    private String bookId;
    private String userId;

    @BeforeEach
    void setUp() {
        bookService = new BookService(new BookValidator());
        userService = new UserService(new UserValidator());
        loanService = new LoanService(bookService, userService, new LoanValidator());

        Book book = bookService.addBook("Clean Code", "Martin", 2);
        User user = userService.registerUser("Juan");
        bookId = book.getId();
        userId = user.getId();
    }

    @Test
    void testCreateLoanSuccess() {
        Loan loan = loanService.createLoan(bookId, userId);
        assertNotNull(loan);
        assertEquals(Loan.Status.ACTIVE, loan.getStatus());
    }

    @Test
    void testCreateLoanBookNotFound() {
        assertThrows(BookNotAvailableException.class,
                () -> loanService.createLoan("id-inexistente", userId));
    }

    @Test
    void testCreateLoanUserNotFound() {
        assertThrows(Exception.class,
                () -> loanService.createLoan(bookId, "id-inexistente"));
    }

    @Test
    void testCreateLoanBookNotAvailable() {
        bookService.updateAvailability(bookId, false);
        assertThrows(BookNotAvailableException.class,
                () -> loanService.createLoan(bookId, userId));
    }

    @Test
    void testLoanLimitExceeded() {
        // Agregar más libros para llegar al límite
        String b2 = bookService.addBook("Refactoring", "Fowler", 1).getId();
        String b3 = bookService.addBook("DDD", "Evans", 1).getId();
        String b4 = bookService.addBook("TDD", "Beck", 1).getId();

        loanService.createLoan(bookId, userId);
        loanService.createLoan(b2, userId);
        loanService.createLoan(b3, userId);

        assertThrows(RuntimeException.class,
                () -> loanService.createLoan(b4, userId));
    }
}
