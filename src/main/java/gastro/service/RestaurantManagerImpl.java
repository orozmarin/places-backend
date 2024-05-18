package gastro.service;

import gastro.model.Restaurant;
import gastro.repository.RestaurantRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestaurantManagerImpl implements RestaurantManager {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Override
    public Restaurant saveOrUpdateRestaurant(Restaurant restaurant) {
        if (restaurant.getId() == null) {
            restaurant.setId(UUID.randomUUID().toString().split("-")[0]);
        }
        restaurant.setRestaurantRating(
                restaurant.getFirstRating().getRestaurantRating() + restaurant.getSecondRating().getRestaurantRating());
        return restaurantRepository.save(restaurant);
    }

    @Override
    public List<Restaurant> findAllRestaurants() {
        return restaurantRepository.findAll();
    }

    @Override
    public Restaurant getRestaurantByName(String name) {
        return restaurantRepository.findRestaurantByName(name);
    }

    @Override
    public List<Restaurant> getRestaurantsByRating(double rating) {
        return restaurantRepository.findByRestaurantRating(rating);
    }

    @Override
    public String deleteTask(String restaurantId) {
        restaurantRepository.deleteById(restaurantId);
        return "Restaurant " + restaurantId + " deleted from database!";
    }
}
