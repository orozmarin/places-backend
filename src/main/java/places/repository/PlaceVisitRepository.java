package places.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import places.model.PlaceVisit;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceVisitRepository extends MongoRepository<PlaceVisit, String> {

    List<PlaceVisit> findByPlaceIdOrderByVisitedAtDesc(String placeId);

    List<PlaceVisit> findByUserId(String userId);

    Optional<PlaceVisit> findFirstByPlaceIdOrderByVisitedAtDesc(String placeId);

    void deleteByPlaceId(String placeId);
}
