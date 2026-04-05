package edu.eci.dosw.infrastructure.mongodb;

import edu.eci.dosw.persistence.entity.Book;
import edu.eci.dosw.persistence.entity.LibraryUser;
import edu.eci.dosw.persistence.entity.Loan;
import edu.eci.dosw.persistence.entity.LoanHistoryEntry;
import edu.eci.dosw.infrastructure.mongodb.document.MongoBookDocument;
import edu.eci.dosw.infrastructure.mongodb.document.MongoLoanDocument;
import edu.eci.dosw.infrastructure.mongodb.document.MongoLoanHistoryEntry;
import edu.eci.dosw.infrastructure.mongodb.document.MongoUserDocument;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MongoProjectionMapper {

    public MongoBookDocument toDocument(Book source, String existingId) {
        MongoBookDocument target = new MongoBookDocument();
        target.setId(existingId);
        target.setRelationalId(source.getId());
        target.setTitle(source.getTitle());
        target.setAuthor(source.getAuthor());
        target.setCategories(source.getCategories());
        target.setPublicationType(source.getPublicationType());
        target.setPublicationDate(source.getPublicationDate());
        target.setIsbn(source.getIsbn());
        target.setPages(source.getPages());
        target.setLanguage(source.getLanguage());
        target.setPublisherCompany(source.getPublisherCompany());
        target.setAvailabilityStatus(source.getAvailabilityStatus());
        target.setTotalCopies(source.getTotalCopies());
        target.setAvailableCopies(source.getAvailableCopies());
        target.setBorrowedCopies(source.getBorrowedCopies());
        target.setAddedToCatalogAt(source.getAddedToCatalogAt());
        target.setSynchronizedAt(LocalDate.now());
        return target;
    }

    public MongoUserDocument toDocument(LibraryUser source, String existingId) {
        MongoUserDocument target = new MongoUserDocument();
        target.setId(existingId);
        target.setRelationalId(source.getId());
        target.setName(source.getName());
        target.setUsername(source.getUsername());
        target.setEmail(source.getEmail());
        target.setMembershipType(source.getMembershipType());
        target.setRole(source.getRole());
        target.setAddedToLibraryAt(source.getAddedToLibraryAt());
        target.setSynchronizedAt(LocalDate.now());
        return target;
    }

    public MongoLoanDocument toDocument(Loan source, String existingId) {
        MongoLoanDocument target = new MongoLoanDocument();
        target.setId(existingId);
        target.setRelationalId(source.getId());
        target.setBookId(source.getBook().getId());
        target.setBookTitle(source.getBook().getTitle());
        target.setUserId(source.getUser().getId());
        target.setUsername(source.getUser().getUsername());
        target.setLoanDate(source.getLoanDate());
        target.setReturnedDate(source.getReturnedDate());
        target.setStatus(source.getStatus());
        target.setHistory(toHistory(source.getHistory()));
        target.setSynchronizedAt(LocalDate.now());
        return target;
    }

    private List<MongoLoanHistoryEntry> toHistory(List<LoanHistoryEntry> history) {
        return history.stream()
                .map(entry -> new MongoLoanHistoryEntry(entry.getStatus(), entry.getExecutedAt()))
                .toList();
    }
}
