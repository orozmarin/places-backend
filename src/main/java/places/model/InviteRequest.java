package places.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InviteRequest {
    private String placeId;
    private String inviteeId;
    private String placeVisitId; // nullable
}
