package edu.eci.dosw.controller.dto.response;

import edu.eci.dosw.core.model.LoanStatus;
import java.time.LocalDate;
import java.util.List;

public record LoanResponse(
        Long id,
        Long bookId,
        String bookTitle,
        Long userId,
        String username,
        LocalDate loanDate,
        LocalDate returnedDate,
        LoanStatus status,
        List<LoanHistoryResponse> history
) {
}
