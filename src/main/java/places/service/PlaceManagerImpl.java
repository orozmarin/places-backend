package places.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import places.model.CoVisitor;
import places.model.Place;
import places.model.PlaceResponse;
import places.model.PlaceSearchForm;
import places.model.UserVisit;
import places.model.VisitStatus;
import places.repository.PlaceRepository;
import places.repository.UserRepository;
import places.repository.UserVisitRepository;

@Service
public class PlaceManagerImpl implements PlaceManager {

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private UserVisitRepository userVisitRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public PlaceResponse saveOrUpdatePlace(Place place) {
        if (place.getId() == null) {
            place.setId(UUID.randomUUID().toString().split("-")[0]);
        }
        Place saved = placeRepository.save(place);
        return PlaceResponse.fromPlace(saved, new ArrayList<>());
    }

    @Override
    public List<PlaceResponse> findAllPlaces(PlaceSearchForm placeSearchForm, String userId) {
        List<Place> places = placeRepository.findPlacesBySearchForm(placeSearchForm, userId);
        return places.stream()
                .map(place -> PlaceResponse.fromPlace(place, assembleCoVisitors(place.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<PlaceResponse> findFavoritePlaces(String userId) {
        List<Place> places = placeRepository.findByUserIdAndIsFavorite(userId, true);
        return places.stream()
                .map(place -> PlaceResponse.fromPlace(place, assembleCoVisitors(place.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<PlaceResponse> findSharedPlaces(String userId) {
        List<UserVisit> visits = userVisitRepository.findByUserIdAndStatus(userId, VisitStatus.VISITED);
        return visits.stream()
                .map(visit -> placeRepository.findById(visit.getPlaceId()).orElse(null))
                .filter(place -> place != null)
                .map(place -> PlaceResponse.fromPlace(place, assembleCoVisitors(place.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public String deletePlace(String placeId) {
        placeRepository.deleteById(placeId);
        return "Place " + placeId + " deleted from database!";
    }

    private List<CoVisitor> assembleCoVisitors(String placeId) {
        List<UserVisit> visits = userVisitRepository.findByPlaceIdAndStatus(placeId, VisitStatus.VISITED);
        return visits.stream()
                .map(visit -> userRepository.findById(visit.getUserId()).orElse(null))
                .filter(user -> user != null)
                .map(user -> CoVisitor.builder()
                        .userId(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .profileImageUrl(user.getProfileImageUrl())
                        .build())
                .collect(Collectors.toList());
    }
}
