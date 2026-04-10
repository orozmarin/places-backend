package places.migration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import places.model.Place;
import places.model.PlaceVisit;
import places.model.UserVisit;
import places.model.VisitInvitation;
import places.model.VisitStatus;
import places.repository.PlaceRepository;
import places.repository.PlaceVisitRepository;
import places.repository.UserVisitRepository;
import places.repository.VisitInvitationRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlaceVisitMigration {

    private final PlaceRepository placeRepository;
    private final PlaceVisitRepository placeVisitRepository;
    private final UserVisitRepository userVisitRepository;
    private final VisitInvitationRepository visitInvitationRepository;

    // @EventListener(ApplicationReadyEvent.class)
    public void migrate() {
        log.info("Starting PlaceVisit migration...");

        // 1. For each Place without visitCount (null): create initial PlaceVisit
        List<Place> places = placeRepository.findAll();
        for (Place place : places) {
            if (place.getVisitCount() == null) {
                log.info("Migrating place: {}", place.getId());

                List<PlaceVisit> existingVisits = placeVisitRepository.findByPlaceIdOrderByVisitedAtDesc(place.getId());
                if (existingVisits.isEmpty()) {
                    PlaceVisit initialVisit = new PlaceVisit();
                    initialVisit.setId(UUID.randomUUID().toString().split("-")[0]);
                    initialVisit.setPlaceId(place.getId());
                    initialVisit.setUserId(place.getUserId());
                    initialVisit.setVisitedAt(place.getVisitedAt());
                    initialVisit.setOwnerRating(place.getRating());
                    placeVisitRepository.save(initialVisit);
                    log.info("Created initial PlaceVisit for place: {}", place.getId());
                }

                // Update place summary fields
                place.setVisitCount(1);
                place.setAverageRating(place.getRating());
                place.setLatestVisitedAt(place.getVisitedAt());
                placeRepository.save(place);
            }
        }

        // 2. For each UserVisit without placeVisitId: find PlaceVisit for the place and link
        List<UserVisit> userVisits = userVisitRepository.findAll();
        for (UserVisit userVisit : userVisits) {
            if (userVisit.getPlaceVisitId() == null && userVisit.getStatus() == VisitStatus.VISITED) {
                Optional<PlaceVisit> placeVisitOpt = placeVisitRepository
                        .findFirstByPlaceIdOrderByVisitedAtDesc(userVisit.getPlaceId());
                placeVisitOpt.ifPresent(pv -> {
                    userVisit.setPlaceVisitId(pv.getId());
                    userVisitRepository.save(userVisit);
                    log.info("Linked UserVisit {} to PlaceVisit {}", userVisit.getId(), pv.getId());
                });
            }
        }

        // 3. For each VisitInvitation without placeVisitId: link to latest PlaceVisit
        List<VisitInvitation> invitations = visitInvitationRepository.findAll();
        for (VisitInvitation invitation : invitations) {
            if (invitation.getPlaceVisitId() == null) {
                Optional<PlaceVisit> placeVisitOpt = placeVisitRepository
                        .findFirstByPlaceIdOrderByVisitedAtDesc(invitation.getPlaceId());
                placeVisitOpt.ifPresent(pv -> {
                    invitation.setPlaceVisitId(pv.getId());
                    visitInvitationRepository.save(invitation);
                    log.info("Linked VisitInvitation {} to PlaceVisit {}", invitation.getId(), pv.getId());
                });
            }
        }

        log.info("PlaceVisit migration complete.");
    }
}
