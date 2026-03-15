package places.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import places.model.CoVisitor;
import places.model.FriendshipStatus;
import places.model.InvitationStatus;
import places.model.Place;
import places.model.PlaceResponse;
import places.model.Rating;
import places.model.User;
import places.model.UserVisit;
import places.model.VisitInvitation;
import places.model.VisitInvitationDto;
import places.model.VisitStatus;
import places.repository.FriendshipRepository;
import places.repository.PlaceRepository;
import places.repository.UserRepository;
import places.repository.UserVisitRepository;
import places.repository.VisitInvitationRepository;

@Service
@RequiredArgsConstructor
public class VisitInvitationManagerImpl implements VisitInvitationManager {

    private final VisitInvitationRepository visitInvitationRepository;
    private final UserVisitRepository userVisitRepository;
    private final FriendshipRepository friendshipRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;

    @Override
    public VisitInvitation sendInvitation(String inviterId, String placeId, String inviteeId) {
        boolean areFriends = friendshipRepository
                .findByRequesterIdAndAddresseeId(inviterId, inviteeId)
                .map(f -> f.getStatus() == FriendshipStatus.ACCEPTED)
                .orElseGet(() -> friendshipRepository
                        .findByRequesterIdAndAddresseeId(inviteeId, inviterId)
                        .map(f -> f.getStatus() == FriendshipStatus.ACCEPTED)
                        .orElse(false));

        if (!areFriends) {
            throw new RuntimeException("Users are not friends");
        }

        if (visitInvitationRepository.existsByPlaceIdAndInviteeIdAndStatus(
                placeId, inviteeId, InvitationStatus.PENDING)) {
            throw new RuntimeException("Invitation already sent");
        }

        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new RuntimeException("Place not found"));

        VisitInvitation invitation = VisitInvitation.builder()
                .id(UUID.randomUUID().toString().split("-")[0])
                .placeId(placeId)
                .placeName(place.getName())
                .inviterId(inviterId)
                .inviteeId(inviteeId)
                .status(InvitationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return visitInvitationRepository.save(invitation);
    }

    @Override
    public List<VisitInvitationDto> getPendingInvitations(String userId) {
        return visitInvitationRepository.findByInviteeIdAndStatus(userId, InvitationStatus.PENDING)
                .stream()
                .map(inv -> {
                    User inviter = userRepository.findById(inv.getInviterId()).orElse(null);
                    return VisitInvitationDto.builder()
                            .id(inv.getId())
                            .placeId(inv.getPlaceId())
                            .placeName(inv.getPlaceName())
                            .inviterId(inv.getInviterId())
                            .inviterName(inviter != null ? inviter.getFullName() : null)
                            .inviterProfileImageUrl(inviter != null ? inviter.getProfileImageUrl() : null)
                            .inviteeId(inv.getInviteeId())
                            .status(inv.getStatus())
                            .createdAt(inv.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public UserVisit acceptInvitation(String invitationId) {
        VisitInvitation invitation = visitInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));

        invitation.setStatus(InvitationStatus.ACCEPTED);
        visitInvitationRepository.save(invitation);

        UserVisit visit = UserVisit.builder()
                .id(UUID.randomUUID().toString().split("-")[0])
                .placeId(invitation.getPlaceId())
                .userId(invitation.getInviteeId())
                .visitedAt(LocalDateTime.now())
                .status(VisitStatus.PENDING)
                .build();

        return userVisitRepository.save(visit);
    }

    @Override
    public void declineInvitation(String invitationId) {
        VisitInvitation invitation = visitInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));
        invitation.setStatus(InvitationStatus.DECLINED);
        visitInvitationRepository.save(invitation);
    }

    @Override
    public PlaceResponse rateVisit(String visitId, Rating rating) {
        UserVisit visit = userVisitRepository.findById(visitId)
                .orElseThrow(() -> new RuntimeException("Visit not found"));

        visit.setRating(rating);
        visit.setStatus(VisitStatus.VISITED);
        userVisitRepository.save(visit);

        Place place = placeRepository.findById(visit.getPlaceId())
                .orElseThrow(() -> new RuntimeException("Place not found"));

        List<UserVisit> visitedVisits = userVisitRepository
                .findByPlaceIdAndStatus(place.getId(), VisitStatus.VISITED);

        List<CoVisitor> coVisitors = visitedVisits.stream()
                .map(v -> userRepository.findById(v.getUserId()).orElse(null))
                .filter(user -> user != null)
                .map(user -> CoVisitor.builder()
                        .userId(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .profileImageUrl(user.getProfileImageUrl())
                        .build())
                .collect(Collectors.toList());

        return PlaceResponse.fromPlace(place, coVisitors);
    }
}
