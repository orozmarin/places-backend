package places.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestDto {
    private String friendshipId;
    private String requesterId;
    private String requesterName;
    private String requesterTag;
    private String requesterUsername;
    private String requesterProfileImageUrl;
    private FriendshipStatus status;
}
