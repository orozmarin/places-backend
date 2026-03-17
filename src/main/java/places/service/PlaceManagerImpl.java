package places.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
        List<UserVisit> myVisits = userVisitRepository.findByUserIdAndStatus(userId, VisitStatus.VISITED);
        return myVisits.stream()
                .map(myVisit -> {
                    Place place = placeRepository.findById(myVisit.getPlaceId()).orElse(null);
                    if (place == null) return null;
                    PlaceResponse response = PlaceResponse.fromPlace(place, assembleCoVisitors(place.getId()));
                    response.setRating(myVisit.getRating());
                    return response;
                })
                .filter(r -> r != null)
                .collect(Collectors.toList());
    }

    @Override
    public String deletePlace(String placeId) {
        placeRepository.deleteById(placeId);
        return "Place " + placeId + " deleted from database!";
    }

    private List<CoVisitor> assembleCoVisitors(String placeId) {
        List<UserVisit> visits = userVisitRepository.findByPlaceIdAndStatus(placeId, VisitStatus.VISITED);
        Set<String> visitUserIds = visits.stream()
                .map(UserVisit::getUserId)
                .collect(Collectors.toSet());

        List<CoVisitor> coVisitors = new ArrayList<>(visits.stream()
                .map(visit -> {
                    var user = userRepository.findById(visit.getUserId()).orElse(null);
                    if (user == null) return null;
                    return CoVisitor.builder()
                            .userId(user.getId())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .profileImageUrl(user.getProfileImageUrl())
                            .rating(visit.getRating())
                            .build();
                })
                .filter(cv -> cv != null)
                .collect(Collectors.toList()));

        // Include the place creator if they have no UserVisit (creators never go through the invitation flow)
        placeRepository.findById(placeId).ifPresent(place -> {
            if (!visitUserIds.contains(place.getUserId())) {
                userRepository.findById(place.getUserId()).ifPresent(creator ->
                        coVisitors.add(CoVisitor.builder()
                                .userId(creator.getId())
                                .firstName(creator.getFirstName())
                                .lastName(creator.getLastName())
                                .profileImageUrl(creator.getProfileImageUrl())
                                .rating(place.getRating())
                                .build())
                );
            }
        });

        return coVisitors;
    }
}
