package places.model;

import java.time.LocalDate;
import lombok.Data;
import places.model.User.Sex;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Sex sex;
    private LocalDate dateOfBirth;
}
