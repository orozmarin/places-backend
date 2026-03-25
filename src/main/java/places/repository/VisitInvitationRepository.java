package places.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import places.model.InvitationStatus;
import places.model.VisitInvitation;

@Repository
public interface VisitInvitationRepository extends MongoRepository<VisitInvitation, String> {
    List<VisitInvitation> findByInviteeIdAndStatus(String inviteeId, InvitationStatus status);
    boolean existsByPlaceIdAndInviteeIdAndStatus(String placeId, String inviteeId, InvitationStatus status);
}
