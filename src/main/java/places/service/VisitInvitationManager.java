package places.service;

import java.util.List;
import places.model.PlaceResponse;
import places.model.Rating;
import places.model.UserVisit;
import places.model.VisitInvitation;
import places.model.VisitInvitationDto;

public interface VisitInvitationManager {
    VisitInvitation sendInvitation(String inviterId, String placeId, String inviteeId, String placeVisitId);
    List<VisitInvitationDto> getPendingInvitations(String userId);
    UserVisit acceptInvitation(String invitationId);
    void declineInvitation(String invitationId);
    PlaceResponse rateVisit(String visitId, Rating rating);
    PlaceResponse removeCoVisitor(String placeId, String coVisitorUserId, String requestingUserId);
}
