package places.service;

import places.model.Place;
import java.util.List;
import places.model.PlaceSearchForm;

public interface PlaceManager {
    Place saveOrUpdatePlace(Place place);
    List<Place> findAllPlaces(PlaceSearchForm placeSearchForm);
    List<Place> findFavoritePlaces();
    String deletePlace(String placeId);

}
