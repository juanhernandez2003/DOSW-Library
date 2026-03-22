package edu.eci.dosw.controller;

import edu.eci.dosw.controller.dto.LoanDTO;
import edu.eci.dosw.core.model.Loan;
import edu.eci.dosw.core.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@Tag(name = "Loans", description = "Operaciones sobre préstamos")
public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @Operation(summary = "Crear un préstamo")
    @PostMapping
    public ResponseEntity<Loan> createLoan(@RequestBody LoanDTO dto) {
        return ResponseEntity.ok(loanService.createLoan(dto.getBookId(), dto.getUserId()));
    }

    @Operation(summary = "Obtener todos los préstamos")
    @GetMapping
    public ResponseEntity<List<Loan>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }
}