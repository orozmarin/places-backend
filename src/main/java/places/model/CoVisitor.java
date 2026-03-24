package places.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CoVisitor {
    private String userId;
    private String firstName;
    private String lastName;
    private String profileImageUrl;
    private Rating rating;
}
