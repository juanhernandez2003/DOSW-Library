package edu.eci.dosw;

import edu.eci.dosw.controller.dto.request.LoginRequest;
import edu.eci.dosw.controller.dto.request.RegisterRequest;
import edu.eci.dosw.core.model.Role;
import edu.eci.dosw.persistence.entity.LibraryUser;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerIT extends AbstractControllerIT {

    @Test
    void registerShouldPersistUserInDatabase() throws Exception {
        RegisterRequest request = new RegisterRequest("Ana", "ana_reg", "Password123*");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.user.username").value("ana_reg"))
                .andExpect(jsonPath("$.user.role").value("USER"));

        LibraryUser savedUser = userRepository.findByUsername("ana_reg").orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals(Role.USER, savedUser.getRole());
    }

    @Test
    void loginShouldAuthenticateExistingUser() throws Exception {
        LibraryUser user = createUser(Role.USER);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(user.getUsername(), "Password123*"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.user.username").value(user.getUsername()));
    }
}
