package edu.eci.dosw;

import edu.eci.dosw.controller.dto.request.LoanRequest;
import edu.eci.dosw.core.model.LoanStatus;
import edu.eci.dosw.core.model.Role;
import edu.eci.dosw.persistence.entity.Book;
import edu.eci.dosw.persistence.entity.LibraryUser;
import edu.eci.dosw.persistence.entity.Loan;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoanControllerIT extends AbstractControllerIT {

    @Test
    void createLoanShouldPersistLoanAndDecreaseStock() throws Exception {
        LibraryUser user = createUser(Role.USER);
        Book book = createBook(5, 3);

        mockMvc.perform(post("/api/loans")
                        .header(AUTHORIZATION, "Bearer " + tokenFor(user))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoanRequest(book.getId()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.history.length()").value(1));

        assertEquals(1, loanRepository.count());
        assertEquals(2, bookRepository.findById(book.getId()).orElseThrow().getAvailableCopies());
    }

    @Test
    void createLoanShouldRejectNonExistingBook() throws Exception {
        LibraryUser user = createUser(Role.USER);

        mockMvc.perform(post("/api/loans")
                        .header(AUTHORIZATION, "Bearer " + tokenFor(user))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoanRequest(999999L))))
                .andExpect(status().isNotFound());
    }

    @Test
    void createLoanShouldRejectBookWithoutAvailability() throws Exception {
        LibraryUser user = createUser(Role.USER);
        Book book = createBook(5, 0);

                mockMvc.perform(post("/api/loans")
                        .header(AUTHORIZATION, "Bearer " + tokenFor(user))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoanRequest(book.getId()))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[0]").value("El libro no tiene unidades disponibles para préstamo"));
    }

    @Test
    void returnLoanShouldUpdateLoanAndRestoreStock() throws Exception {
        LibraryUser user = createUser(Role.USER);
        Book book = createBook(5, 2);
        Loan loan = createLoan(user, book, LoanStatus.ACTIVE);
        book.setAvailableCopies(1);
        book.setBorrowedCopies(4);
        bookRepository.save(book);

        mockMvc.perform(patch("/api/loans/{id}/return", loan.getId())
                        .header(AUTHORIZATION, "Bearer " + tokenFor(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETURNED"))
                .andExpect(jsonPath("$.history.length()").value(2));

        Loan updatedLoan = loanRepository.findById(loan.getId()).orElseThrow();
        assertEquals(LoanStatus.RETURNED, updatedLoan.getStatus());
        assertEquals(2, bookRepository.findById(book.getId()).orElseThrow().getAvailableCopies());
    }

    @Test
    void returnLoanShouldRejectAlreadyReturnedLoan() throws Exception {
        LibraryUser user = createUser(Role.USER);
        Book book = createBook(5, 2);
        Loan loan = createLoan(user, book, LoanStatus.RETURNED);

        mockMvc.perform(patch("/api/loans/{id}/return", loan.getId())
                        .header(AUTHORIZATION, "Bearer " + tokenFor(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[0]").value("No se puede devolver un préstamo que ya fue devuelto"));
    }

    @Test
    void returnLoanShouldRejectAnotherUser() throws Exception {
        LibraryUser owner = createUser(Role.USER);
        LibraryUser otherUser = createUser(Role.USER);
        Book book = createBook(5, 1);
        Loan loan = createLoan(owner, book, LoanStatus.ACTIVE);

        mockMvc.perform(patch("/api/loans/{id}/return", loan.getId())
                        .header(AUTHORIZATION, "Bearer " + tokenFor(otherUser)))
                .andExpect(status().isForbidden());
    }

    @Test
    void myLoansShouldReturnOnlyAuthenticatedUserLoans() throws Exception {
        LibraryUser user = createUser(Role.USER);
        Book book = createBook(3, 3);
        createLoan(user, book, LoanStatus.ACTIVE);

        mockMvc.perform(get("/api/loans/me")
                        .header(AUTHORIZATION, "Bearer " + tokenFor(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void findAllShouldReturnAllLoansForLibrarian() throws Exception {
        LibraryUser librarian = createUser(Role.LIBRARIAN);
        LibraryUser user = createUser(Role.USER);
        Book book = createBook(3, 2);
        createLoan(user, book, LoanStatus.ACTIVE);

        mockMvc.perform(get("/api/loans")
                        .header(AUTHORIZATION, "Bearer " + tokenFor(librarian)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void findAllShouldRejectNormalUser() throws Exception {
        LibraryUser user = createUser(Role.USER);

        mockMvc.perform(get("/api/loans")
                        .header(AUTHORIZATION, "Bearer " + tokenFor(user)))
                .andExpect(status().isForbidden());
    }
}
