package edu.eci.dosw;

import edu.eci.dosw.controller.dto.request.RegisterRequest;
import edu.eci.dosw.controller.dto.request.RoleUpdateRequest;
import edu.eci.dosw.core.model.Role;
import edu.eci.dosw.persistence.entity.LibraryUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIT extends AbstractControllerIT {

    @Test
    void createShouldPersistUserForLibrarian() throws Exception {
        LibraryUser librarian = createUser(Role.LIBRARIAN);
        RegisterRequest request = sampleRegisterRequest("Ana", "ana_reg");

        mockMvc.perform(post("/api/users")
                        .header(AUTHORIZATION, "Bearer " + tokenFor(librarian))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("ana_reg"))
                .andExpect(jsonPath("$.email").value("ana_reg@mail.com"))
                .andExpect(jsonPath("$.membershipType").value("STANDARD"))
                .andExpect(jsonPath("$.role").value("USER"));

        assertEquals(Role.USER, userRepository.findByUsername("ana_reg").orElseThrow().getRole());
    }

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
    void findAllShouldRejectNormalUser() throws Exception {
        LibraryUser user = createUser(Role.USER);

        mockMvc.perform(get("/api/users")
                        .header(AUTHORIZATION, "Bearer " + tokenFor(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createShouldRejectNormalUser() throws Exception {
        LibraryUser user = createUser(Role.USER);
        RegisterRequest request = sampleRegisterRequest("Ana", "ana_reg_user");

        mockMvc.perform(post("/api/users")
                        .header(AUTHORIZATION, "Bearer " + tokenFor(user))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void createShouldRejectWeakPassword() throws Exception {
        LibraryUser librarian = createUser(Role.LIBRARIAN);
        RegisterRequest request = new RegisterRequest(
                "Ana Maria",
                "ana_weak",
                "ana_weak@mail.com",
                "password",
                edu.eci.dosw.core.model.MembershipType.STANDARD
        );

        mockMvc.perform(post("/api/users")
                        .header(AUTHORIZATION, "Bearer " + tokenFor(librarian))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void meShouldReturnAuthenticatedUser() throws Exception {
        LibraryUser user = createUser(Role.USER);

        mockMvc.perform(get("/api/users/me")
                        .header(AUTHORIZATION, "Bearer " + tokenFor(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
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

    @Test
    void updateRoleShouldRejectNormalUser() throws Exception {
        LibraryUser requester = createUser(Role.USER);
        LibraryUser user = createUser(Role.USER);

        mockMvc.perform(patch("/api/users/{id}/role", user.getId())
                        .header(AUTHORIZATION, "Bearer " + tokenFor(requester))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RoleUpdateRequest(Role.LIBRARIAN))))
                .andExpect(status().isForbidden());
    }
}
