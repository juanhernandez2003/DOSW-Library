package edu.eci.dosw.core.service;

import edu.eci.dosw.controller.dto.request.RegisterRequest;
import edu.eci.dosw.core.exception.BusinessRuleException;
import edu.eci.dosw.core.exception.ResourceNotFoundException;
import edu.eci.dosw.core.model.Role;
import edu.eci.dosw.infrastructure.mongodb.DualPersistenceSyncService;
import edu.eci.dosw.persistence.entity.LibraryUser;
import edu.eci.dosw.persistence.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DualPersistenceSyncService dualPersistenceSyncService;

    public List<LibraryUser> findAll() {
        return userRepository.findAll();
    }

    public LibraryUser findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    public LibraryUser findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    @Transactional
    public LibraryUser create(RegisterRequest request) {
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
        return savedUser;
    }

    @Transactional
    public LibraryUser updateRole(Long id, Role role) {
        LibraryUser user = findById(id);
        user.setRole(role);
        LibraryUser savedUser = userRepository.save(user);
        dualPersistenceSyncService.syncUser(savedUser);
        return savedUser;
    }
}
