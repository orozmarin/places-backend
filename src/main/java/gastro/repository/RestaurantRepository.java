package gastro.repository;

import gastro.model.Restaurant;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends MongoRepository<Restaurant, String> {
    @Query("{name:'?0'}")
    Restaurant findRestaurantByName(String name);
    List<Restaurant> findByRestaurantRating(double rating);
}
