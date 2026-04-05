package edu.eci.dosw.infrastructure.mongodb.repository;

import edu.eci.dosw.infrastructure.mongodb.document.MongoLoanDocument;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoLoanRepository extends MongoRepository<MongoLoanDocument, String> {

    Optional<MongoLoanDocument> findByRelationalId(Long relationalId);
}
