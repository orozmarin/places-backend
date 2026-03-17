package places.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import places.model.UserVisit;
import places.model.VisitStatus;

@Repository
public interface UserVisitRepository extends MongoRepository<UserVisit, String> {
    List<UserVisit> findByPlaceIdAndStatus(String placeId, VisitStatus status);
    List<UserVisit> findByUserIdAndStatus(String userId, VisitStatus status);
    boolean existsByPlaceIdAndUserId(String placeId, String userId);
    void deleteByPlaceIdAndUserId(String placeId, String userId);
}
