package places.rest;

import static places.constants.Constants.REST_URL;

import places.model.Place;
import places.model.PlaceSearchForm;
import places.service.PlaceManager;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
public class PlaceController {

    public static final String SAVE_OR_UPDATE_PLACE = "/places/save-or-update";
    public static final String FIND_ALL_PLACES = "/places/find";
    public static final String DELETE_PLACE = "/places/delete/{placeId}";
    public static final String FIND_FAVORITE_PLACES = "/places/find/favorites";

    @Autowired
    private PlaceManager placeManager;

    @PostMapping(value = SAVE_OR_UPDATE_PLACE)
    @ResponseStatus(HttpStatus.CREATED)
    public Place saveOrUpdatePlace(@RequestBody Place place) {
        return placeManager.saveOrUpdatePlace(place);
    }

    @GetMapping(value = FIND_ALL_PLACES)
    public List<Place> getPlaces(@RequestBody PlaceSearchForm placeSearchForm) {
        return placeManager.findAllPlaces(placeSearchForm);
    }

    @PostMapping(value = DELETE_PLACE)
    public String deletePLace(@PathVariable String placeId) {
        return placeManager.deletePlace(placeId);
    }

    @GetMapping(value = FIND_FAVORITE_PLACES)
    public List<Place> getFavoritedPlaces() {
        return placeManager.findFavoritePlaces();
    }

}
