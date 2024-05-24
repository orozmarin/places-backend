package places.repository;

import places.model.Place;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import places.repository.qdls.PlaceRepositoryCustom;

@Repository
public interface PlaceRepository extends MongoRepository<Place, String>, PlaceRepositoryCustom {

    @Query("{name:'?0'}")
    Place findPlaceByName(String name);

    List<Place> findByPlaceRating(double rating);
}
