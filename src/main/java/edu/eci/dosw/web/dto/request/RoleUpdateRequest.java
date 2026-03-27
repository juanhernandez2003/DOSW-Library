package edu.eci.dosw.controller.dto.request;

import edu.eci.dosw.core.model.Role;
import jakarta.validation.constraints.NotNull;

public record RoleUpdateRequest(
        @NotNull(message = "El rol es obligatorio")
        Role role
) {
}
