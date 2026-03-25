package places.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Id;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
@Data
public class User implements Serializable {

    @Id
    private String id;
    private String email;

    @JsonIgnore
    private String password;

    private String firstName;
    private String lastName;
    private String username;
    private String tag;

    private Sex sex;
    private LocalDate dateOfBirth;
    private UserStatus status;
    private String profileImageUrl;
    private AuthProvider authProvider;

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }


    public enum UserStatus {
        WAITING_FIRST_LOGIN,
        ACTIVE,
        BLOCKED,
        DELETED
    }

    public enum Sex {
        MALE,
        FEMALE,
        UNDEFINED
    }

    public enum AuthProvider {
        EMAIL,
        GOOGLE,
        APPLE
    }
}
