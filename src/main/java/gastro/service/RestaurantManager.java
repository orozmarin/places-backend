package gastro.service;

import gastro.model.Restaurant;
import java.util.List;

public interface RestaurantManager {
    Restaurant saveOrUpdateRestaurant(Restaurant restaurant);
    List<Restaurant> findAllRestaurants();
    Restaurant getRestaurantByName(String name);
    List<Restaurant> getRestaurantsByRating(double rating);
    String deleteTask(String restaurantId);

}
