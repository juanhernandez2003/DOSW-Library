package edu.eci.dosw.controller.dto.response;

import edu.eci.dosw.core.model.MembershipType;
import edu.eci.dosw.core.model.Role;
import java.time.LocalDate;

public record UserResponse(
        Long id,
        String name,
        String username,
        String email,
        MembershipType membershipType,
        Role role,
        LocalDate addedToLibraryAt
) {
}
