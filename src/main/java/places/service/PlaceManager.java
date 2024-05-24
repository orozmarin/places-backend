package places.service;

import places.model.Place;
import java.util.List;
import places.model.PlaceSearchForm;

public interface PlaceManager {
    Place saveOrUpdatePlace(Place place);
    List<Place> findAllPlaces(PlaceSearchForm placeSearchForm);
    Place getPlaceByName(String name);
    List<Place> getPlaceByRating(double rating);
    String deletePlace(String placeId);

}
