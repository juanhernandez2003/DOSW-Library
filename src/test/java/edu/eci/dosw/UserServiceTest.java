package edu.eci.dosw;

import edu.eci.dosw.core.exception.UserNotFoundException;
import edu.eci.dosw.core.model.User;
import edu.eci.dosw.core.service.UserService;
import edu.eci.dosw.core.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(new UserValidator());
    }

    @Test
    void testRegisterUserSuccess() {
        User user = userService.registerUser("Juan");
        assertNotNull(user.getId());
        assertEquals("Juan", user.getName());
    }

    @Test
    void testGetUserByIdNotFound() {
        assertThrows(UserNotFoundException.class,
                () -> userService.getUserById("id-inexistente"));
    }

    @Test
    void testGetAllUsers() {
        userService.registerUser("Ana");
        userService.registerUser("Pedro");
        assertEquals(2, userService.getAllUsers().size());
    }

    @Test
    void testRegisterUserEmptyNameThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(""));
    }
}
