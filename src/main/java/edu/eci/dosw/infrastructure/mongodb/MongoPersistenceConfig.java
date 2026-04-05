package edu.eci.dosw.infrastructure.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import edu.eci.dosw.infrastructure.mongodb.repository.MongoBookRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
@ConditionalOnProperty(prefix = "app.persistence.mongo", name = "enabled", havingValue = "true")
@EnableMongoRepositories(basePackageClasses = MongoBookRepository.class)
public class MongoPersistenceConfig {

    @Bean
    public MongoClient mongoClient(@Value("${spring.data.mongodb.uri}") String mongoUri) {
        return MongoClients.create(mongoUri);
    }

    @Bean
    public MongoTemplate mongoTemplate(
            MongoClient mongoClient,
            @Value("${spring.data.mongodb.uri}") String mongoUri,
            @Value("${app.persistence.mongo.database}") String database
    ) {
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(mongoClient, database));
    }
}
