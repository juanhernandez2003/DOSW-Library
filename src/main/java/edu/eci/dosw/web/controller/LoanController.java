package edu.eci.dosw.controller;

import edu.eci.dosw.controller.dto.request.LoanRequest;
import edu.eci.dosw.controller.dto.response.LoanResponse;
import edu.eci.dosw.core.service.LoanService;
import edu.eci.dosw.infrastructure.security.AuthenticatedUser;
import edu.eci.dosw.persistence.mapper.LoanMapper;
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
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Tag(name = "Loans", description = "Gestión de préstamos y devoluciones")
public class LoanController {

    private final LoanService loanService;
    private final LoanMapper loanMapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'LIBRARIAN')")
    @Operation(summary = "Solicitar un préstamo")
    public ResponseEntity<LoanResponse> createLoan(
            @Valid @RequestBody LoanRequest request,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(loanMapper.toResponse(loanService.createLoan(authenticatedUser.getId(), request.bookId())));
    }

    @PatchMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('USER', 'LIBRARIAN')")
    @Operation(summary = "Devolver un libro")
    public ResponseEntity<LoanResponse> returnLoan(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return ResponseEntity.ok(loanMapper.toResponse(loanService.returnLoan(id, authenticatedUser.getId())));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'LIBRARIAN')")
    @Operation(summary = "Consultar mis préstamos")
    public ResponseEntity<List<LoanResponse>> myLoans(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return ResponseEntity.ok(loanMapper.toResponseList(loanService.findByUser(authenticatedUser.getId())));
    }

    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    @Operation(summary = "Consultar todos los préstamos")
    public ResponseEntity<List<LoanResponse>> findAll() {
        return ResponseEntity.ok(loanMapper.toResponseList(loanService.findAll()));
    }
}
