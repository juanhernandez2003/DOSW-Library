package edu.eci.dosw.core.service;

import edu.eci.dosw.controller.dto.request.LoginRequest;
import edu.eci.dosw.controller.dto.request.RegisterRequest;
import edu.eci.dosw.controller.dto.response.AuthResponse;
import edu.eci.dosw.core.exception.BusinessRuleException;
import edu.eci.dosw.core.model.Role;
import edu.eci.dosw.infrastructure.mongodb.DualPersistenceSyncService;
import edu.eci.dosw.infrastructure.security.AuthenticatedUser;
import edu.eci.dosw.infrastructure.security.JwtService;
import edu.eci.dosw.persistence.entity.LibraryUser;
import edu.eci.dosw.persistence.mapper.UserMapper;
import edu.eci.dosw.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final DualPersistenceSyncService dualPersistenceSyncService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessRuleException("El nombre de usuario ya existe");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("El correo electronico ya existe");
        }

        LibraryUser user = new LibraryUser();
        user.setName(request.name());
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setMembershipType(request.membershipType());
        user.setRole(Role.USER);
        user.setAddedToLibraryAt(java.time.LocalDate.now());
        LibraryUser savedUser = userRepository.save(user);
        dualPersistenceSyncService.syncUser(savedUser);

        AuthenticatedUser principal = new AuthenticatedUser(savedUser);
        return new AuthResponse(jwtService.generateToken(principal), "Bearer", userMapper.toResponse(savedUser));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        LibraryUser user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessRuleException("Credenciales inválidas"));

        return new AuthResponse(
                jwtService.generateToken(new AuthenticatedUser(user)),
                "Bearer",
                userMapper.toResponse(user)
        );
    }
}
