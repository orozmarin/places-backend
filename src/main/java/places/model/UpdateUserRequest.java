package places.model;

import java.time.LocalDate;
import lombok.Data;
import places.model.User.Sex;

@Data
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String username;
    private Sex sex;
    private LocalDate dateOfBirth;
}
