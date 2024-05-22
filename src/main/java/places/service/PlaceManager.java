package places.service;

import places.model.Place;
import java.util.List;

public interface PlaceManager {
    Place saveOrUpdatePlace(Place place);
    List<Place> findAllPlaces();
    Place getPlaceByName(String name);
    List<Place> getPlaceByRating(double rating);
    String deletePlace(String placeId);

}
