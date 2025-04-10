package places.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import places.model.Place;
import places.model.PlaceSearchForm;
import places.repository.PlaceRepository;

@Service
public class PlaceManagerImpl implements PlaceManager {

    @Autowired
    private PlaceRepository placeRepository;

    @Override
    public Place saveOrUpdatePlace(Place place) {
        if (place.getId() == null) {
            place.setId(UUID.randomUUID().toString().split("-")[0]);
        }
        place.setPlaceRating(
                place.getFirstRating().getPlaceRating() + place.getSecondRating().getPlaceRating());
        return placeRepository.save(place);
    }

    @Override
    public List<Place> findAllPlaces(PlaceSearchForm placeSearchForm) {
        return placeRepository.findPlacesBySearchForm(placeSearchForm);
    }

    @Override
    public List<Place> findFavoritePlaces(){
        return placeRepository.findByIsFavorite(true);
    }

    @Override
    public String deletePlace(String placeId) {
        placeRepository.deleteById(placeId);
        return "Place " + placeId + " deleted from database!";
    }
}
