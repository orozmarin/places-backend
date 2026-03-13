package places.service;

import java.util.List;
import places.model.PlaceResponse;
import places.model.Rating;
import places.model.UserVisit;
import places.model.VisitInvitation;

public interface VisitInvitationManager {
    VisitInvitation sendInvitation(String inviterId, String placeId, String inviteeId);
    List<VisitInvitation> getPendingInvitations(String userId);
    UserVisit acceptInvitation(String invitationId);
    void declineInvitation(String invitationId);
    PlaceResponse rateVisit(String visitId, Rating rating);
}
