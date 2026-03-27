package edu.eci.dosw;

import edu.eci.dosw.controller.dto.request.RegisterRequest;
import edu.eci.dosw.controller.dto.response.UserResponse;
import edu.eci.dosw.core.exception.BusinessRuleException;
import edu.eci.dosw.core.model.Role;
import edu.eci.dosw.core.service.AuthService;
import edu.eci.dosw.infrastructure.security.JwtService;
import edu.eci.dosw.persistence.entity.LibraryUser;
import edu.eci.dosw.persistence.mapper.UserMapper;
import edu.eci.dosw.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private UserMapper userMapper;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        jwtService = Mockito.mock(JwtService.class);
        userMapper = Mockito.mock(UserMapper.class);
        authService = new AuthService(userRepository, passwordEncoder, authenticationManager, jwtService, userMapper);
    }

    @Test
    void shouldRejectDuplicatedUsername() {
        when(userRepository.existsByUsername("juan")).thenReturn(true);

        RegisterRequest request = new RegisterRequest("Juan", "juan", "Password123");
        assertThrows(BusinessRuleException.class, () -> authService.register(request));
    }

    @Test
    void shouldRegisterUserWithRoleUser() {
        RegisterRequest request = new RegisterRequest("Ana", "ana01", "Password123");
        LibraryUser savedUser = new LibraryUser();
        savedUser.setId(1L);
        savedUser.setName("Ana");
        savedUser.setUsername("ana01");
        savedUser.setPassword("encoded");
        savedUser.setRole(Role.USER);

        when(userRepository.existsByUsername("ana01")).thenReturn(false);
        when(passwordEncoder.encode("Password123")).thenReturn("encoded");
        when(userRepository.save(any(LibraryUser.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any())).thenReturn("token");
        when(userMapper.toResponse(savedUser)).thenReturn(new UserResponse(1L, "Ana", "ana01", Role.USER));

        assertEquals(Role.USER, authService.register(request).user().role());
    }
}
