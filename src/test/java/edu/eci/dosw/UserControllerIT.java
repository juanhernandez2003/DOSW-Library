package edu.eci.dosw;

import edu.eci.dosw.controller.dto.request.RoleUpdateRequest;
import edu.eci.dosw.core.model.Role;
import edu.eci.dosw.persistence.entity.LibraryUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIT extends AbstractControllerIT {

    @Test
    void findAllShouldReturnUsersForLibrarian() throws Exception {
        LibraryUser librarian = createUser(Role.LIBRARIAN);
        createUser(Role.USER);

        mockMvc.perform(get("/api/users")
                        .header(AUTHORIZATION, "Bearer " + tokenFor(librarian)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void meShouldReturnAuthenticatedUser() throws Exception {
        LibraryUser user = createUser(Role.USER);

        mockMvc.perform(get("/api/users/me")
                        .header(AUTHORIZATION, "Bearer " + tokenFor(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()));
    }

    @Test
    void updateRoleShouldModifyUserInDatabase() throws Exception {
        LibraryUser librarian = createUser(Role.LIBRARIAN);
        LibraryUser user = createUser(Role.USER);

        mockMvc.perform(patch("/api/users/{id}/role", user.getId())
                        .header(AUTHORIZATION, "Bearer " + tokenFor(librarian))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RoleUpdateRequest(Role.LIBRARIAN))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("LIBRARIAN"));

        assertEquals(Role.LIBRARIAN, userRepository.findById(user.getId()).orElseThrow().getRole());
    }
}
