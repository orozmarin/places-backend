package places.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "friendships")
@Data
public class Friendship {

    @Id
    private String id;
    private String requesterId;
    private String addresseeId;
    private FriendshipStatus status;
    private LocalDateTime createdAt;
}
