package edu.eci.dosw.controller.dto.response;

public record AuthResponse(
        String token,
        String tokenType,
        UserResponse user
) {
}
