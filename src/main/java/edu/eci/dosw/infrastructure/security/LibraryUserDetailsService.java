package edu.eci.dosw.infrastructure.security;

import edu.eci.dosw.core.exception.ResourceNotFoundException;
import edu.eci.dosw.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LibraryUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public AuthenticatedUser loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(AuthenticatedUser::new)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}
