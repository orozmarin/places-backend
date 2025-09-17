package places.service;

import places.model.Place;
import java.util.List;
import places.model.PlaceSearchForm;

public interface PlaceManager {
    Place saveOrUpdatePlace(Place place);
    List<Place> findAllPlaces(PlaceSearchForm placeSearchForm, String userId);
    List<Place> findFavoritePlaces(String userId);
    String deletePlace(String placeId);

}
