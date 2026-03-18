package places.service;

import places.model.Place;
import places.model.PlaceResponse;
import java.util.List;
import places.model.PlaceSearchForm;

public interface PlaceManager {
    PlaceResponse saveOrUpdatePlace(Place place);
    List<PlaceResponse> findAllPlaces(PlaceSearchForm placeSearchForm, String userId);
    List<PlaceResponse> findFavoritePlaces(String userId);
    List<PlaceResponse> findSharedPlaces(String userId);
    String deletePlace(String placeId, String requestingUserId);
    void acknowledgeOwnershipTransfer(String placeId);
}
