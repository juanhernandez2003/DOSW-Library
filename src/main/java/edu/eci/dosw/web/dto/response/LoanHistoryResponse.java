package edu.eci.dosw.controller.dto.response;

import edu.eci.dosw.core.model.LoanStatus;
import java.time.LocalDateTime;

public record LoanHistoryResponse(
        LoanStatus status,
        LocalDateTime executedAt
) {
}
