package edu.eci.dosw.controller;

import edu.eci.dosw.controller.dto.request.RegisterRequest;
import edu.eci.dosw.controller.dto.request.RoleUpdateRequest;
import edu.eci.dosw.controller.dto.response.UserResponse;
import edu.eci.dosw.core.service.UserService;
import edu.eci.dosw.infrastructure.security.AuthenticatedUser;
import edu.eci.dosw.persistence.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Gestión de usuarios")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    @Operation(summary = "Registrar un nuevo usuario")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponse(userService.create(request)));
    }

    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    @Operation(summary = "Consultar todos los usuarios")
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll().stream().map(userMapper::toResponse).toList());
    }

    @GetMapping("/me")
    @Operation(summary = "Consultar el usuario autenticado")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return ResponseEntity.ok(userMapper.toResponse(authenticatedUser.getUser()));
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('LIBRARIAN')")
    @Operation(summary = "Actualizar el rol de un usuario")
    public ResponseEntity<UserResponse> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequest request
    ) {
        return ResponseEntity.ok(userMapper.toResponse(userService.updateRole(id, request.role())));
    }
}
