package edu.eci.dosw;

import edu.eci.dosw.controller.dto.request.BookRequest;
import edu.eci.dosw.core.model.Role;
import edu.eci.dosw.persistence.entity.Book;
import edu.eci.dosw.persistence.entity.LibraryUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookControllerIT extends AbstractControllerIT {

    @Test
    void createShouldPersistBook() throws Exception {
        LibraryUser librarian = createUser(Role.LIBRARIAN);
        BookRequest request = new BookRequest("Clean Code", "Martin", 5, 5);

        mockMvc.perform(post("/api/books")
                        .header(AUTHORIZATION, "Bearer " + tokenFor(librarian))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Clean Code"));

        assertEquals(1, bookRepository.count());
        assertEquals(5, bookRepository.findAll().get(0).getAvailableCopies());
    }

    @Test
    void createShouldRejectUserWithoutLibrarianRole() throws Exception {
        LibraryUser user = createUser(Role.USER);
        BookRequest request = new BookRequest("Clean Code", "Martin", 5, 5);

        mockMvc.perform(post("/api/books")
                        .header(AUTHORIZATION, "Bearer " + tokenFor(user))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void createShouldRejectUnauthenticatedRequest() throws Exception {
        BookRequest request = new BookRequest("Clean Code", "Martin", 5, 5);

        mockMvc.perform(post("/api/books")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void createShouldRejectInvalidToken() throws Exception {
        BookRequest request = new BookRequest("Clean Code", "Martin", 5, 5);

        mockMvc.perform(post("/api/books")
                        .header(AUTHORIZATION, "Bearer token-invalido")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void createShouldRejectInvalidStock() throws Exception {
        LibraryUser librarian = createUser(Role.LIBRARIAN);
        BookRequest request = new BookRequest("Clean Code", "Martin", 0, 0);

        mockMvc.perform(post("/api/books")
                        .header(AUTHORIZATION, "Bearer " + tokenFor(librarian))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.details[0]").value("totalCopies: El stock total debe ser mayor a 0"));
    }

    @Test
    void findAllShouldReturnPersistedBooks() throws Exception {
        LibraryUser user = createUser(Role.USER);
        createBook(3, 3);

        mockMvc.perform(get("/api/books")
                        .header(AUTHORIZATION, "Bearer " + tokenFor(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void findAvailableShouldReturnOnlyBooksWithStock() throws Exception {
        LibraryUser user = createUser(Role.USER);
        createBook(2, 1);
        createBook(4, 0);

        mockMvc.perform(get("/api/books/available")
                        .header(AUTHORIZATION, "Bearer " + tokenFor(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void findByIdShouldReturnBookFromDatabase() throws Exception {
        LibraryUser user = createUser(Role.USER);
        Book book = createBook(4, 2);

        mockMvc.perform(get("/api/books/{id}", book.getId())
                        .header(AUTHORIZATION, "Bearer " + tokenFor(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(book.getId()))
                .andExpect(jsonPath("$.availableCopies").value(2));
    }

    @Test
    void updateShouldModifyBookInDatabase() throws Exception {
        LibraryUser librarian = createUser(Role.LIBRARIAN);
        Book book = createBook(3, 2);
        BookRequest request = new BookRequest("Updated", "Autor", 7, 4);

        mockMvc.perform(put("/api/books/{id}", book.getId())
                        .header(AUTHORIZATION, "Bearer " + tokenFor(librarian))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCopies").value(7))
                .andExpect(jsonPath("$.availableCopies").value(4));

        Book updated = bookRepository.findById(book.getId()).orElseThrow();
        assertEquals(7, updated.getTotalCopies());
        assertEquals(4, updated.getAvailableCopies());
    }

    @Test
    void updateShouldRejectAvailableCopiesGreaterThanTotal() throws Exception {
        LibraryUser librarian = createUser(Role.LIBRARIAN);
        Book book = createBook(3, 2);
        BookRequest request = new BookRequest("Updated", "Autor", 4, 5);

        mockMvc.perform(put("/api/books/{id}", book.getId())
                        .header(AUTHORIZATION, "Bearer " + tokenFor(librarian))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[0]").value("Los ejemplares disponibles no pueden superar el stock total"));
    }
}
