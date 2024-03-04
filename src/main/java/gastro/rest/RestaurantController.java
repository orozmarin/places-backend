package gastro.rest;

import static gastro.constants.Constants.REST_URL;

import gastro.model.Restaurant;
import gastro.service.RestaurantManager;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = REST_URL)
public class RestaurantController {

    public static final String SAVE_OR_UPDATE_RESTAURANT = "/restaurants/save-or-update";
    public static final String FIND_ALL_RESTAURANTS = "/restaurants/find";
    public static final String FIND_RESTAURANT_BY_NAME = "/restaurants/find/{name}";
    public static final String FIND_RESTAURANT_BY_RATING = "/restaurants/find/rating/{rating}";
    public static final String DELETE_RESTAURANT = "/restaurants/delete/{id}";

    @Autowired
    private RestaurantManager restaurantManager;

    @PostMapping(value = SAVE_OR_UPDATE_RESTAURANT)
    @ResponseStatus(HttpStatus.CREATED)
    public Restaurant createRestaurant(@RequestBody Restaurant restaurant) {
        return restaurantManager.saveOrUpdateRestaurant(restaurant);
    }

    @GetMapping(value = FIND_ALL_RESTAURANTS)
    public List<Restaurant> getRestaurants() {
        return restaurantManager.findAllRestaurants();
    }

    @GetMapping(value = FIND_RESTAURANT_BY_NAME)
    public Restaurant getRestaurant(@PathVariable String name) {
        return restaurantManager.getRestaurantByName(name);
    }

    @GetMapping(value = FIND_RESTAURANT_BY_RATING)
    public List<Restaurant> getRestaurantsByRating(@PathVariable double rating) {
        return restaurantManager.getRestaurantsByRating(rating);
    }

    @DeleteMapping(value = DELETE_RESTAURANT)
    public String deleteTask(@PathVariable String id) {
        return restaurantManager.deleteTask(id);
    }
}
