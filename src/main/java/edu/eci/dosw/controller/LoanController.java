package edu.eci.dosw.controller;

import edu.eci.dosw.controller.dto.LoanDTO;
import edu.eci.dosw.core.model.Loan;
import edu.eci.dosw.core.service.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<Loan> createLoan(@RequestBody LoanDTO dto) {
        return ResponseEntity.ok(loanService.createLoan(dto.getBookId(), dto.getUserId()));
    }

    @GetMapping
    public ResponseEntity<List<Loan>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }
}