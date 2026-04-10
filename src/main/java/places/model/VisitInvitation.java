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
@Document(collection = "visit_invitations")
@Data
public class VisitInvitation {

    @Id
    private String id;
    private String placeId;
    private String placeName;
    private String inviterId;
    private String inviteeId;
    private InvitationStatus status;
    private LocalDateTime createdAt;
    private String placeVisitId; // nullable, backward compat
}
