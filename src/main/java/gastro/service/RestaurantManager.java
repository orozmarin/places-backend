package gastro.service;

import gastro.model.Restaurant;
import gastro.repository.RestaurantRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestaurantManager {
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Transactional
    public Restaurant saveOrUpdateRestaurant(Restaurant restaurant){
        restaurant.setId(UUID.randomUUID().toString().split("-")[0]);
        return restaurantRepository.save(restaurant);
    }
    @Transactional
    public List<Restaurant> findAllRestaurants(){
        return restaurantRepository.findAll();
    }
    @Transactional
    public Restaurant getRestaurantByName(String name){
        return restaurantRepository.findRestaurantByName(name);
    }
    @Transactional
    public List<Restaurant> getRestaurantByRating(double rating){
        return restaurantRepository.findByRestaurantRating(rating);
    }
    @Transactional
    public String deleteTask(String restaurantId){
        restaurantRepository.deleteById(restaurantId);
        return "Restaurant " + restaurantId + " deleted from database!";
    }
}
