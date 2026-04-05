package edu.eci.dosw;

import edu.eci.dosw.core.exception.BusinessRuleException;
import edu.eci.dosw.core.model.BookAvailabilityStatus;
import edu.eci.dosw.core.model.LoanStatus;
import edu.eci.dosw.core.model.MembershipType;
import edu.eci.dosw.core.model.PublicationType;
import edu.eci.dosw.core.model.Role;
import edu.eci.dosw.core.service.LoanService;
import edu.eci.dosw.infrastructure.mongodb.DualPersistenceSyncService;
import edu.eci.dosw.persistence.entity.Book;
import edu.eci.dosw.persistence.entity.LibraryUser;
import edu.eci.dosw.persistence.entity.Loan;
import edu.eci.dosw.persistence.repository.BookRepository;
import edu.eci.dosw.persistence.repository.LoanRepository;
import edu.eci.dosw.persistence.repository.UserRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LoanServiceTest {

    private LoanRepository loanRepository;
    private BookRepository bookRepository;
    private UserRepository userRepository;
    private DualPersistenceSyncService dualPersistenceSyncService;
    private LoanService loanService;
    private LibraryUser user;
    private Book book;

    @BeforeEach
    void setUp() {
        loanRepository = Mockito.mock(LoanRepository.class);
        bookRepository = Mockito.mock(BookRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        dualPersistenceSyncService = Mockito.mock(DualPersistenceSyncService.class);
        loanService = new LoanService(loanRepository, bookRepository, userRepository, dualPersistenceSyncService);

        user = new LibraryUser();
        user.setId(1L);
        user.setName("Juan");
        user.setUsername("juan");
        user.setEmail("juan@mail.com");
        user.setPassword("encoded");
        user.setMembershipType(MembershipType.STANDARD);
        user.setRole(Role.USER);
        user.setAddedToLibraryAt(LocalDate.now());

        book = new Book();
        book.setId(10L);
        book.setTitle("Refactoring");
        book.setAuthor("Fowler");
        book.setCategories(Set.of("Software"));
        book.setPublicationType(PublicationType.BOOK);
        book.setPublicationDate(LocalDate.now().minusYears(1));
        book.setIsbn("9780201485677");
        book.setPages(450);
        book.setLanguage("EN");
        book.setPublisherCompany("Addison-Wesley");
        book.setAvailabilityStatus(BookAvailabilityStatus.AVAILABLE);
        book.setTotalCopies(4);
        book.setAvailableCopies(2);
        book.setBorrowedCopies(2);
        book.setAddedToCatalogAt(LocalDate.now());
    }

    @Test
    void shouldDecreaseStockWhenLoanIsCreated() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(loanRepository.countByUserAndStatus(user, LoanStatus.ACTIVE)).thenReturn(0L);
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Loan loan = loanService.createLoan(1L, 10L);

        assertEquals(LoanStatus.ACTIVE, loan.getStatus());
        assertEquals(1, book.getAvailableCopies());
    }

    @Test
    void shouldRejectLoanWithoutAvailableStock() {
        book.setAvailableCopies(0);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        assertThrows(BusinessRuleException.class, () -> loanService.createLoan(1L, 10L));
    }

    @Test
    void shouldRejectReturnedLoanTwice() {
        Loan loan = new Loan();
        loan.setId(7L);
        loan.setBook(book);
        loan.setUser(user);
        loan.setStatus(LoanStatus.RETURNED);

        when(loanRepository.findById(7L)).thenReturn(Optional.of(loan));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(BusinessRuleException.class, () -> loanService.returnLoan(7L, 1L));
    }
}
