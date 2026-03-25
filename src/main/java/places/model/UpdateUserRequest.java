package places.model;

import java.time.LocalDate;
import lombok.Data;
import places.model.User.Sex;

@Data
public class UpdateUserRequest {
    private String username;
    private LocalDate dateOfBirth;
    private Sex sex;
}
