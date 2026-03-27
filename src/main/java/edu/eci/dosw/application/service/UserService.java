package edu.eci.dosw.core.service;

import edu.eci.dosw.core.exception.ResourceNotFoundException;
import edu.eci.dosw.core.model.Role;
import edu.eci.dosw.persistence.entity.LibraryUser;
import edu.eci.dosw.persistence.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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
    public LibraryUser updateRole(Long id, Role role) {
        LibraryUser user = findById(id);
        user.setRole(role);
        return userRepository.save(user);
    }
}
