package edu.eci.dosw.config;

import edu.eci.dosw.core.model.MembershipType;
import edu.eci.dosw.core.model.Role;
import edu.eci.dosw.persistence.entity.LibraryUser;
import edu.eci.dosw.persistence.repository.UserRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.existsByUsername("bibliotecario")) {
            return;
        }

        LibraryUser librarian = new LibraryUser();
        librarian.setName("Bibliotecario");
        librarian.setUsername("bibliotecario");
        librarian.setEmail("bibliotecario@library.local");
        librarian.setPassword(passwordEncoder.encode("Biblioteca123*"));
        librarian.setMembershipType(MembershipType.PLATINUM);
        librarian.setRole(Role.LIBRARIAN);
        librarian.setAddedToLibraryAt(LocalDate.now());
        userRepository.save(librarian);
    }
}
