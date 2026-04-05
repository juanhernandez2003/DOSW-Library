package edu.eci.dosw.infrastructure.mongodb.document;

import edu.eci.dosw.core.model.LoanStatus;
import java.time.LocalDateTime;

public record MongoLoanHistoryEntry(
        LoanStatus status,
        LocalDateTime executedAt
) {
}
