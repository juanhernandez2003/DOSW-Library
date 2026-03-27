package edu.eci.dosw.controller.dto.response;

import edu.eci.dosw.core.model.Role;

public record UserResponse(
        Long id,
        String name,
        String username,
        Role role
) {
}
