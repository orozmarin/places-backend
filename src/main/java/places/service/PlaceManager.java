package places.service;

import places.model.Place;
import places.model.PlaceResponse;
import places.model.PlaceVisit;
import places.model.PlaceVisitResponse;
import java.util.List;
import places.model.PlaceSearchForm;

public interface PlaceManager {
    PlaceResponse saveOrUpdatePlace(Place place);
    List<PlaceResponse> findAllPlaces(PlaceSearchForm placeSearchForm, String userId);
    List<PlaceResponse> findFavoritePlaces(String userId);
    List<PlaceResponse> findSharedPlaces(String userId);
    String deletePlace(String placeId, String requestingUserId);
    void acknowledgeOwnershipTransfer(String placeId);

    // PlaceVisit operations
    PlaceVisitResponse createPlaceVisit(PlaceVisit placeVisit);
    List<PlaceVisitResponse> getPlaceVisits(String placeId);
    PlaceVisitResponse updatePlaceVisit(String visitId, PlaceVisit updates);
    void deletePlaceVisit(String visitId);
    String uploadPlaceVisitPhoto(String visitId, byte[] fileBytes, String fileName) throws java.io.IOException;
    String uploadConsumedItemPhoto(String visitId, String itemId, byte[] fileBytes, String fileName) throws java.io.IOException;
}
