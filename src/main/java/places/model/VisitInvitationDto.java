package places.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisitInvitationDto {
    private String id;
    private String placeId;
    private String placeName;
    private String inviterId;
    private String inviterName;
    private String inviterProfileImageUrl;
    private String inviteeId;
    private InvitationStatus status;
    private LocalDateTime createdAt;
}