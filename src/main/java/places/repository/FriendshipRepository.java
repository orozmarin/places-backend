package places.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import places.model.Friendship;
import places.model.FriendshipStatus;

@Repository
public interface FriendshipRepository extends MongoRepository<Friendship, String> {
    List<Friendship> findByAddresseeIdAndStatus(String addresseeId, FriendshipStatus status);
    List<Friendship> findByRequesterIdAndStatus(String requesterId, FriendshipStatus status);
    Optional<Friendship> findByRequesterIdAndAddresseeId(String requesterId, String addresseeId);
    List<Friendship> findByAddresseeId(String addresseeId);
}
