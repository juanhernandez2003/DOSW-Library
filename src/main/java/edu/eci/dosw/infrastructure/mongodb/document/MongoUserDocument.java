package edu.eci.dosw.infrastructure.mongodb.document;

import edu.eci.dosw.core.model.MembershipType;
import edu.eci.dosw.core.model.Role;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "users_catalog")
public class MongoUserDocument {

    @Id
    private String id;

    private Long relationalId;
    private String name;
    private String username;
    private String email;
    private MembershipType membershipType;
    private Role role;
    private LocalDate addedToLibraryAt;
    private LocalDate synchronizedAt;
}
