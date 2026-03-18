package places.rest;

import static places.constants.Constants.REST_URL;

import places.model.Place;
import places.model.PlaceResponse;
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
    public static final String FIND_ALL_PLACES = "/places/find/{userId}";
    public static final String DELETE_PLACE = "/places/delete/{placeId}/{requestingUserId}";
    public static final String ACKNOWLEDGE_TRANSFER = "/places/{placeId}/acknowledge-transfer";
    public static final String FIND_FAVORITE_PLACES = "/places/find/favorites/{userId}";
    public static final String FIND_SHARED_PLACES = "/places/find/shared/{userId}";

    @Autowired
    private PlaceManager placeManager;

    @PostMapping(value = SAVE_OR_UPDATE_PLACE)
    @ResponseStatus(HttpStatus.CREATED)
    public PlaceResponse saveOrUpdatePlace(@RequestBody Place place) {
        return placeManager.saveOrUpdatePlace(place);
    }

    @GetMapping(value = FIND_ALL_PLACES)
    public List<PlaceResponse> getPlaces(@RequestBody PlaceSearchForm placeSearchForm, @PathVariable String userId) {
        return placeManager.findAllPlaces(placeSearchForm, userId);
    }

    @PostMapping(value = DELETE_PLACE)
    public String deletePLace(@PathVariable String placeId, @PathVariable String requestingUserId) {
        return placeManager.deletePlace(placeId, requestingUserId);
    }

    @PostMapping(value = ACKNOWLEDGE_TRANSFER)
    public void acknowledgeOwnershipTransfer(@PathVariable String placeId) {
        placeManager.acknowledgeOwnershipTransfer(placeId);
    }

    @GetMapping(value = FIND_FAVORITE_PLACES)
    public List<PlaceResponse> getFavoritedPlaces(@PathVariable String userId) {
        return placeManager.findFavoritePlaces(userId);
    }

    @GetMapping(value = FIND_SHARED_PLACES)
    public List<PlaceResponse> getSharedPlaces(@PathVariable String userId) {
        return placeManager.findSharedPlaces(userId);
    }
}
