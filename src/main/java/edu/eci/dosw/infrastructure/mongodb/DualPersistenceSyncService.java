package edu.eci.dosw.infrastructure.mongodb;

import edu.eci.dosw.infrastructure.mongodb.repository.MongoBookRepository;
import edu.eci.dosw.infrastructure.mongodb.repository.MongoLoanRepository;
import edu.eci.dosw.infrastructure.mongodb.repository.MongoUserRepository;
import edu.eci.dosw.persistence.entity.Book;
import edu.eci.dosw.persistence.entity.LibraryUser;
import edu.eci.dosw.persistence.entity.Loan;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DualPersistenceSyncService {

    @Value("${app.persistence.mongo.enabled:false}")
    private boolean mongoEnabled;

    private final ObjectProvider<MongoBookRepository> mongoBookRepositoryProvider;
    private final ObjectProvider<MongoUserRepository> mongoUserRepositoryProvider;
    private final ObjectProvider<MongoLoanRepository> mongoLoanRepositoryProvider;
    private final MongoProjectionMapper mapper;

    public void syncBook(Book book) {
        if (!mongoEnabled) {
            return;
        }
        MongoBookRepository repository = mongoBookRepositoryProvider.getIfAvailable();
        if (repository == null) {
            return;
        }
        String existingId = repository.findByRelationalId(book.getId()).map(doc -> doc.getId()).orElse(null);
        repository.save(mapper.toDocument(book, existingId));
    }

    public void syncUser(LibraryUser user) {
        if (!mongoEnabled) {
            return;
        }
        MongoUserRepository repository = mongoUserRepositoryProvider.getIfAvailable();
        if (repository == null) {
            return;
        }
        String existingId = repository.findByRelationalId(user.getId()).map(doc -> doc.getId()).orElse(null);
        repository.save(mapper.toDocument(user, existingId));
    }

    public void syncLoan(Loan loan) {
        if (!mongoEnabled) {
            return;
        }
        MongoLoanRepository repository = mongoLoanRepositoryProvider.getIfAvailable();
        if (repository == null) {
            return;
        }
        String existingId = repository.findByRelationalId(loan.getId()).map(doc -> doc.getId()).orElse(null);
        repository.save(mapper.toDocument(loan, existingId));
    }
}
