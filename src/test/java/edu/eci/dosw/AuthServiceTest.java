package edu.eci.dosw;

import edu.eci.dosw.controller.dto.request.RegisterRequest;
import edu.eci.dosw.controller.dto.response.UserResponse;
import edu.eci.dosw.core.exception.BusinessRuleException;
import edu.eci.dosw.core.model.MembershipType;
import edu.eci.dosw.core.model.Role;
import edu.eci.dosw.core.service.AuthService;
import edu.eci.dosw.infrastructure.mongodb.DualPersistenceSyncService;
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
    private DualPersistenceSyncService dualPersistenceSyncService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        jwtService = Mockito.mock(JwtService.class);
        userMapper = Mockito.mock(UserMapper.class);
        dualPersistenceSyncService = Mockito.mock(DualPersistenceSyncService.class);
        authService = new AuthService(userRepository, passwordEncoder, authenticationManager, jwtService, userMapper, dualPersistenceSyncService);
    }

    @Test
    void shouldRejectDuplicatedUsername() {
        when(userRepository.existsByUsername("juan")).thenReturn(true);

        RegisterRequest request = new RegisterRequest("Juan", "juan", "juan@mail.com", "Password123*", MembershipType.STANDARD);
        assertThrows(BusinessRuleException.class, () -> authService.register(request));
    }

    @Test
    void shouldRegisterUserWithRoleUser() {
        RegisterRequest request = new RegisterRequest("Ana", "ana01", "ana@mail.com", "Password123*", MembershipType.STANDARD);
        LibraryUser savedUser = new LibraryUser();
        savedUser.setId(1L);
        savedUser.setName("Ana");
        savedUser.setUsername("ana01");
        savedUser.setEmail("ana@mail.com");
        savedUser.setPassword("encoded");
        savedUser.setMembershipType(MembershipType.STANDARD);
        savedUser.setRole(Role.USER);
        savedUser.setAddedToLibraryAt(java.time.LocalDate.now());

        when(userRepository.existsByUsername("ana01")).thenReturn(false);
        when(userRepository.existsByEmail("ana@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123*")).thenReturn("encoded");
        when(userRepository.save(any(LibraryUser.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any())).thenReturn("token");
        when(userMapper.toResponse(savedUser)).thenReturn(new UserResponse(
                1L,
                "Ana",
                "ana01",
                "ana@mail.com",
                MembershipType.STANDARD,
                Role.USER,
                savedUser.getAddedToLibraryAt()
        ));

        assertEquals(Role.USER, authService.register(request).user().role());
    }
}
