package edu.eci.dosw;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.dosw.controller.dto.request.BookRequest;
import edu.eci.dosw.controller.dto.request.RegisterRequest;
import edu.eci.dosw.core.model.BookAvailabilityStatus;
import edu.eci.dosw.core.model.LoanStatus;
import edu.eci.dosw.core.model.MembershipType;
import edu.eci.dosw.core.model.PublicationType;
import edu.eci.dosw.core.model.Role;
import edu.eci.dosw.infrastructure.security.AuthenticatedUser;
import edu.eci.dosw.infrastructure.security.JwtService;
import edu.eci.dosw.persistence.entity.Book;
import edu.eci.dosw.persistence.entity.LibraryUser;
import edu.eci.dosw.persistence.entity.Loan;
import edu.eci.dosw.persistence.entity.LoanHistoryEntry;
import edu.eci.dosw.persistence.repository.BookRepository;
import edu.eci.dosw.persistence.repository.LoanRepository;
import edu.eci.dosw.persistence.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class AbstractControllerIT {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected BookRepository bookRepository;

    @Autowired
    protected LoanRepository loanRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JwtService jwtService;

    protected LibraryUser createUser(Role role) {
        LibraryUser user = new LibraryUser();
        user.setName(role == Role.LIBRARIAN ? "Bibliotecario Test" : "Usuario Test");
        user.setUsername(role.name().toLowerCase() + "_" + UUID.randomUUID().toString().substring(0, 8));
        user.setEmail(user.getUsername() + "@mail.com");
        user.setPassword(passwordEncoder.encode("Password123*"));
        user.setMembershipType(MembershipType.STANDARD);
        user.setRole(role);
        user.setAddedToLibraryAt(LocalDate.now());
        return userRepository.save(user);
    }

    protected String tokenFor(LibraryUser user) {
        return jwtService.generateToken(new AuthenticatedUser(user));
    }

    protected Book createBook(int totalCopies, int availableCopies) {
        Book book = new Book();
        book.setTitle("Libro " + UUID.randomUUID().toString().substring(0, 5));
        book.setAuthor("Autor Test");
        book.setCategories(new HashSet<>(Set.of("Tecnologia")));
        book.setPublicationType(PublicationType.BOOK);
        book.setPublicationDate(LocalDate.now().minusYears(1));
        book.setIsbn("978" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));
        book.setPages(200);
        book.setLanguage("ES");
        book.setPublisherCompany("Editorial Test");
        book.setTotalCopies(totalCopies);
        book.setAvailableCopies(availableCopies);
        book.setBorrowedCopies(totalCopies - availableCopies);
        book.setAvailabilityStatus(availableCopies == 0 ? BookAvailabilityStatus.OUT_OF_STOCK : BookAvailabilityStatus.AVAILABLE);
        book.setAddedToCatalogAt(LocalDate.now());
        return bookRepository.save(book);
    }

    protected Loan createLoan(LibraryUser user, Book book, LoanStatus status) {
        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(LocalDate.now());
        loan.setStatus(status);
        loan.getHistory().add(new LoanHistoryEntry(LoanStatus.ACTIVE, LocalDateTime.now().minusDays(1)));
        if (status == LoanStatus.RETURNED) {
            loan.setReturnedDate(LocalDate.now());
            loan.getHistory().add(new LoanHistoryEntry(LoanStatus.RETURNED, LocalDateTime.now()));
        }
        return loanRepository.save(loan);
    }

    protected BookRequest sampleBookRequest(String title, String author, int totalCopies, int availableCopies) {
        return new BookRequest(
                title,
                author,
                new HashSet<>(Set.of("Tecnologia", "Biblioteca")),
                PublicationType.BOOK,
                LocalDate.now().minusYears(2),
                "978" + UUID.randomUUID().toString().replace("-", "").substring(0, 10),
                250,
                "ES",
                "Editorial Test",
                totalCopies,
                availableCopies
        );
    }

    protected RegisterRequest sampleRegisterRequest(String name, String username) {
        return new RegisterRequest(
                name,
                username,
                username + "@mail.com",
                "Password123*",
                MembershipType.STANDARD
        );
    }
}
