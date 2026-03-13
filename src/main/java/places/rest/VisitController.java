package places.rest;

import static places.constants.Constants.REST_URL;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import places.model.InviteRequest;
import places.model.PlaceResponse;
import places.model.Rating;
import places.model.User;
import places.model.UserVisit;
import places.model.VisitInvitation;
import places.service.VisitInvitationManager;

@Slf4j
@RestController
@RequestMapping(value = REST_URL)
public class VisitController {

    public static final String SEND_INVITATION = "/visits/invite";
    public static final String GET_PENDING_INVITATIONS = "/visits/invitations/pending/{userId}";
    public static final String ACCEPT_INVITATION = "/visits/invitations/{invitationId}/accept";
    public static final String DECLINE_INVITATION = "/visits/invitations/{invitationId}/decline";
    public static final String RATE_VISIT = "/visits/{visitId}/rate";

    @Autowired
    private VisitInvitationManager visitInvitationManager;

    @PostMapping(value = SEND_INVITATION)
    public VisitInvitation sendInvitation(@RequestBody InviteRequest request) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return visitInvitationManager.sendInvitation(
                loggedUser.getId(), request.getPlaceId(), request.getInviteeId());
    }

    @GetMapping(value = GET_PENDING_INVITATIONS)
    public List<VisitInvitation> getPendingInvitations(@PathVariable String userId) {
        return visitInvitationManager.getPendingInvitations(userId);
    }

    @PostMapping(value = ACCEPT_INVITATION)
    public UserVisit acceptInvitation(@PathVariable String invitationId) {
        return visitInvitationManager.acceptInvitation(invitationId);
    }

    @PostMapping(value = DECLINE_INVITATION)
    public void declineInvitation(@PathVariable String invitationId) {
        visitInvitationManager.declineInvitation(invitationId);
    }

    @PostMapping(value = RATE_VISIT)
    public PlaceResponse rateVisit(@PathVariable String visitId, @RequestBody Rating rating) {
        return visitInvitationManager.rateVisit(visitId, rating);
    }
}
