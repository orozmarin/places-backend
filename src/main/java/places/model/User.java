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
import places.utils.UtilsHelper;

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

    private Sex sex;
    private LocalDate dateOfBirth;
    private UserStatus status;

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public String getUserInitials() {
        String firstNameInitial = UtilsHelper.extractFirstLetter(this.firstName);
        String lastNameInitial = UtilsHelper.extractFirstLetter(this.lastName);
        return firstNameInitial + lastNameInitial;
    }


    public String logUserData() {
        return "User (Id:" + this.getId() + ", Name:" + this.getFullName() + ", Email:" + this.getEmail() + ") ";
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
}
