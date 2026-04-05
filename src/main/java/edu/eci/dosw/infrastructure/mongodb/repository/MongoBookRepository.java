package edu.eci.dosw.infrastructure.mongodb.repository;

import edu.eci.dosw.infrastructure.mongodb.document.MongoBookDocument;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoBookRepository extends MongoRepository<MongoBookDocument, String> {

    Optional<MongoBookDocument> findByRelationalId(Long relationalId);
}
