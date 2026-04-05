package edu.eci.dosw.infrastructure.mongodb.document;

import edu.eci.dosw.core.model.LoanStatus;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "loans_catalog")
public class MongoLoanDocument {

    @Id
    private String id;

    private Long relationalId;
    private Long bookId;
    private String bookTitle;
    private Long userId;
    private String username;
    private LocalDate loanDate;
    private LocalDate returnedDate;
    private LoanStatus status;
    private List<MongoLoanHistoryEntry> history;
    private LocalDate synchronizedAt;
}
