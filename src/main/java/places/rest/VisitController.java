package places.rest;

import static places.constants.Constants.REST_URL;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import places.model.InviteRequest;
import places.model.PlaceResponse;
import places.model.PlaceVisit;
import places.model.PlaceVisitResponse;
import places.model.Rating;
import places.model.User;
import places.model.UserVisit;
import places.model.VisitInvitation;
import places.model.VisitInvitationDto;
import places.service.PlaceManager;
import places.service.VisitInvitationManager;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = REST_URL)
public class VisitController {

    public static final String SEND_INVITATION = "/visits/invite";
    public static final String GET_PENDING_INVITATIONS = "/visits/invitations/pending/{userId}";
    public static final String ACCEPT_INVITATION = "/visits/invitations/{invitationId}/accept";
    public static final String DECLINE_INVITATION = "/visits/invitations/{invitationId}/decline";
    public static final String RATE_VISIT = "/visits/{visitId}/rate";
    public static final String REMOVE_CO_VISITOR = "/visits/{placeId}/co-visitors/{coVisitorUserId}/remove";
    public static final String PLACE_VISITS = "/visits/place-visits";
    public static final String PLACE_VISITS_BY_PLACE = "/visits/place-visits/{placeId}";
    public static final String PLACE_VISIT_BY_ID = "/visits/place-visits/visit/{visitId}";
    public static final String PLACE_VISIT_PHOTOS = "/visits/place-visits/visit/{visitId}/photos";
    public static final String CONSUMED_ITEM_PHOTOS = "/visits/place-visits/visit/{visitId}/items/{itemId}/photos";

    private final VisitInvitationManager visitInvitationManager;
    private final PlaceManager placeManager;

    @PostMapping(value = SEND_INVITATION)
    public VisitInvitation sendInvitation(@RequestBody InviteRequest request) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return visitInvitationManager.sendInvitation(
                loggedUser.getId(), request.getPlaceId(), request.getInviteeId(), request.getPlaceVisitId());
    }

    @GetMapping(value = GET_PENDING_INVITATIONS)
    public List<VisitInvitationDto> getPendingInvitations(@PathVariable String userId) {
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

    @PostMapping(value = REMOVE_CO_VISITOR)
    public PlaceResponse removeCoVisitor(@PathVariable String placeId, @PathVariable String coVisitorUserId) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return visitInvitationManager.removeCoVisitor(placeId, coVisitorUserId, loggedUser.getId());
    }

    // ─── PlaceVisit endpoints ────────────────────────────────────────────────

    @PostMapping(value = PLACE_VISITS)
    @ResponseStatus(HttpStatus.CREATED)
    public PlaceVisitResponse createPlaceVisit(@RequestBody PlaceVisit placeVisit) {
        return placeManager.createPlaceVisit(placeVisit);
    }

    @GetMapping(value = PLACE_VISITS_BY_PLACE)
    public List<PlaceVisitResponse> getPlaceVisits(@PathVariable String placeId) {
        return placeManager.getPlaceVisits(placeId);
    }

    @PatchMapping(value = PLACE_VISIT_BY_ID)
    public PlaceVisitResponse updatePlaceVisit(@PathVariable String visitId, @RequestBody PlaceVisit updates) {
        return placeManager.updatePlaceVisit(visitId, updates);
    }

    @DeleteMapping(value = PLACE_VISIT_BY_ID)
    public void deletePlaceVisit(@PathVariable String visitId) {
        placeManager.deletePlaceVisit(visitId);
    }

    @PostMapping(value = PLACE_VISIT_PHOTOS)
    public ResponseEntity<Map<String, String>> uploadPlaceVisitPhoto(
            @PathVariable String visitId,
            @RequestParam("file") MultipartFile file) throws IOException {
        String url = placeManager.uploadPlaceVisitPhoto(visitId, file.getBytes(), file.getOriginalFilename());
        return ResponseEntity.ok(Map.of("photoUrl", url));
    }

    @PostMapping(value = CONSUMED_ITEM_PHOTOS)
    public ResponseEntity<Map<String, String>> uploadConsumedItemPhoto(
            @PathVariable String visitId,
            @PathVariable String itemId,
            @RequestParam("file") MultipartFile file) throws IOException {
        String url = placeManager.uploadConsumedItemPhoto(visitId, itemId, file.getBytes(), file.getOriginalFilename());
        return ResponseEntity.ok(Map.of("photoUrl", url));
    }
}
