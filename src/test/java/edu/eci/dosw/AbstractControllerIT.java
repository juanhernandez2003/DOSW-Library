package edu.eci.dosw;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.dosw.core.model.LoanStatus;
import edu.eci.dosw.core.model.Role;
import edu.eci.dosw.infrastructure.security.AuthenticatedUser;
import edu.eci.dosw.infrastructure.security.JwtService;
import edu.eci.dosw.persistence.entity.Book;
import edu.eci.dosw.persistence.entity.LibraryUser;
import edu.eci.dosw.persistence.entity.Loan;
import edu.eci.dosw.persistence.repository.BookRepository;
import edu.eci.dosw.persistence.repository.LoanRepository;
import edu.eci.dosw.persistence.repository.UserRepository;
import java.time.LocalDate;
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
        user.setPassword(passwordEncoder.encode("Password123*"));
        user.setRole(role);
        return userRepository.save(user);
    }

    protected String tokenFor(LibraryUser user) {
        return jwtService.generateToken(new AuthenticatedUser(user));
    }

    protected Book createBook(int totalCopies, int availableCopies) {
        Book book = new Book();
        book.setTitle("Libro " + UUID.randomUUID().toString().substring(0, 5));
        book.setAuthor("Autor Test");
        book.setTotalCopies(totalCopies);
        book.setAvailableCopies(availableCopies);
        return bookRepository.save(book);
    }

    protected Loan createLoan(LibraryUser user, Book book, LoanStatus status) {
        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(LocalDate.now());
        loan.setStatus(status);
        if (status == LoanStatus.RETURNED) {
            loan.setReturnedDate(LocalDate.now());
        }
        return loanRepository.save(loan);
    }
}
