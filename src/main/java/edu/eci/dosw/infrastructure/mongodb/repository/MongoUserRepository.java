package edu.eci.dosw.infrastructure.mongodb.repository;

import edu.eci.dosw.infrastructure.mongodb.document.MongoUserDocument;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoUserRepository extends MongoRepository<MongoUserDocument, String> {

    Optional<MongoUserDocument> findByRelationalId(Long relationalId);
}
