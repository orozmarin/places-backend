package places.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import places.model.CoVisitor;
import places.model.ConsumedItem;
import places.model.Place;
import places.model.PlaceResponse;
import places.model.PlaceSearchForm;
import places.model.PlaceVisit;
import places.model.PlaceVisitResponse;
import places.model.Rating;
import places.model.UserVisit;
import places.model.VisitStatus;
import places.repository.PlaceRepository;
import places.repository.PlaceVisitRepository;
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

    @Autowired
    private PlaceVisitRepository placeVisitRepository;

    @Override
    public PlaceResponse saveOrUpdatePlace(Place place) {
        boolean isNew = place.getId() == null;
        if (isNew) {
            place.setId(UUID.randomUUID().toString().split("-")[0]);
        }
        Place saved = placeRepository.save(place);

        if (isNew && saved.getRating() != null && saved.getVisitedAt() != null) {
            PlaceVisit initialVisit = new PlaceVisit();
            initialVisit.setId(UUID.randomUUID().toString().split("-")[0]);
            initialVisit.setPlaceId(saved.getId());
            initialVisit.setUserId(saved.getUserId());
            initialVisit.setVisitedAt(saved.getVisitedAt());
            initialVisit.setOwnerRating(saved.getRating());
            placeVisitRepository.save(initialVisit);
            computePlaceSummary(saved.getId());
            saved = placeRepository.findById(saved.getId()).orElse(saved);
        }

        return PlaceResponse.fromPlace(saved, new ArrayList<>());
    }

    @Override
    public List<PlaceResponse> findAllPlaces(PlaceSearchForm placeSearchForm, String userId) {
        List<Place> places = placeRepository.findPlacesBySearchForm(placeSearchForm, userId);
        return places.stream()
                .map(place -> {
                    PlaceResponse response = PlaceResponse.fromPlace(place, assembleCoVisitors(place.getId()));
                    response.setVisits(getPlaceVisits(place.getId()));
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<PlaceResponse> findFavoritePlaces(String userId) {
        List<Place> places = placeRepository.findByUserIdAndIsFavorite(userId, true);
        return places.stream()
                .map(place -> {
                    PlaceResponse response = PlaceResponse.fromPlace(place, assembleCoVisitors(place.getId()));
                    response.setVisits(getPlaceVisits(place.getId()));
                    return response;
                })
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
                    response.setVisitId(myVisit.getId());
                    response.setVisits(getPlaceVisits(place.getId()));
                    return response;
                })
                .filter(r -> r != null)
                .collect(Collectors.toList());
    }

    @Override
    public String deletePlace(String placeId, String requestingUserId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new RuntimeException("Place not found: " + placeId));

        if (!place.getUserId().equals(requestingUserId)) {
            throw new RuntimeException("Not authorized to delete this place");
        }

        List<UserVisit> coVisitorVisits = userVisitRepository.findByPlaceIdAndStatus(placeId, VisitStatus.VISITED);
        coVisitorVisits.sort(Comparator.comparing(UserVisit::getVisitedAt,
                Comparator.nullsLast(Comparator.naturalOrder())));

        if (coVisitorVisits.isEmpty()) {
            placeVisitRepository.deleteByPlaceId(placeId);
            userVisitRepository.deleteByPlaceId(placeId);
            placeRepository.deleteById(placeId);
            return "DELETED";
        }

        UserVisit newOwnerVisit = coVisitorVisits.get(0);
        String previousHostName = userRepository.findById(requestingUserId)
                .map(u -> u.getFirstName() + " " + u.getLastName())
                .orElse("Unknown");

        place.setUserId(newOwnerVisit.getUserId());
        place.setRating(newOwnerVisit.getRating());
        place.setOwnershipTransferredFromName(previousHostName);
        place.setOwnershipTransferredAt(LocalDateTime.now());
        placeRepository.save(place);

        userVisitRepository.deleteByPlaceIdAndUserId(placeId, newOwnerVisit.getUserId());

        return "TRANSFERRED";
    }

    @Override
    public void acknowledgeOwnershipTransfer(String placeId) {
        placeRepository.findById(placeId).ifPresent(place -> {
            place.setOwnershipTransferredFromName(null);
            place.setOwnershipTransferredAt(null);
            placeRepository.save(place);
        });
    }

    // ─── PlaceVisit CRUD ──────────────────────────────────────────────────────

    @Override
    public PlaceVisitResponse createPlaceVisit(PlaceVisit placeVisit) {
        placeVisit.setId(UUID.randomUUID().toString().split("-")[0]);
        if (placeVisit.getConsumedItems() != null) {
            for (ConsumedItem item : placeVisit.getConsumedItems()) {
                if (item.getId() == null) {
                    item.setId(UUID.randomUUID().toString().split("-")[0]);
                }
            }
        }
        PlaceVisit saved = placeVisitRepository.save(placeVisit);
        computePlaceSummary(saved.getPlaceId());
        return new PlaceVisitResponse(saved, new ArrayList<>());
    }

    @Override
    public List<PlaceVisitResponse> getPlaceVisits(String placeId) {
        List<PlaceVisit> visits = placeVisitRepository.findByPlaceIdOrderByVisitedAtDesc(placeId);
        return visits.stream()
                .map(visit -> {
                    List<UserVisit> userVisits = userVisitRepository.findByPlaceIdAndStatus(placeId, VisitStatus.VISITED)
                            .stream()
                            .filter(uv -> visit.getId().equals(uv.getPlaceVisitId()))
                            .collect(Collectors.toList());
                    List<CoVisitor> coVisitors = buildCoVisitorsFromUserVisits(userVisits);
                    return new PlaceVisitResponse(visit, coVisitors);
                })
                .collect(Collectors.toList());
    }

    @Override
    public PlaceVisitResponse updatePlaceVisit(String visitId, PlaceVisit updates) {
        PlaceVisit existing = placeVisitRepository.findById(visitId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PlaceVisit not found: " + visitId));

        if (updates.getVisitedAt() != null) existing.setVisitedAt(updates.getVisitedAt());
        if (updates.getOwnerRating() != null) existing.setOwnerRating(updates.getOwnerRating());
        if (updates.getPhotoUrls() != null) existing.setPhotoUrls(updates.getPhotoUrls());
        if (updates.getConsumedItems() != null) {
            for (ConsumedItem item : updates.getConsumedItems()) {
                if (item.getId() == null) {
                    item.setId(UUID.randomUUID().toString().split("-")[0]);
                }
            }
            existing.setConsumedItems(updates.getConsumedItems());
        }

        PlaceVisit saved = placeVisitRepository.save(existing);
        computePlaceSummary(saved.getPlaceId());

        List<UserVisit> userVisits = userVisitRepository.findByPlaceIdAndStatus(saved.getPlaceId(), VisitStatus.VISITED)
                .stream()
                .filter(uv -> visitId.equals(uv.getPlaceVisitId()))
                .collect(Collectors.toList());
        List<CoVisitor> coVisitors = buildCoVisitorsFromUserVisits(userVisits);
        return new PlaceVisitResponse(saved, coVisitors);
    }

    @Override
    public void deletePlaceVisit(String visitId) {
        PlaceVisit existing = placeVisitRepository.findById(visitId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PlaceVisit not found: " + visitId));

        String placeId = existing.getPlaceId();
        long visitCount = placeVisitRepository.findByPlaceIdOrderByVisitedAtDesc(placeId).size();
        if (visitCount <= 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete the only visit for this place");
        }

        // Delete linked UserVisit records
        List<UserVisit> linkedUserVisits = userVisitRepository.findByPlaceIdAndStatus(placeId, VisitStatus.VISITED)
                .stream()
                .filter(uv -> visitId.equals(uv.getPlaceVisitId()))
                .collect(Collectors.toList());
        userVisitRepository.deleteAll(linkedUserVisits);

        placeVisitRepository.deleteById(visitId);
        computePlaceSummary(placeId);
    }

    @Override
    public String uploadPlaceVisitPhoto(String visitId, byte[] fileBytes, String fileName) throws IOException {
        PlaceVisit visit = placeVisitRepository.findById(visitId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PlaceVisit not found: " + visitId));

        String url = saveFile(visitId + "_" + fileName, fileBytes);

        List<String> photoUrls = visit.getPhotoUrls() != null ? new ArrayList<>(visit.getPhotoUrls()) : new ArrayList<>();
        photoUrls.add(url);
        visit.setPhotoUrls(photoUrls);
        placeVisitRepository.save(visit);

        return url;
    }

    @Override
    public String uploadConsumedItemPhoto(String visitId, String itemId, byte[] fileBytes, String fileName) throws IOException {
        PlaceVisit visit = placeVisitRepository.findById(visitId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PlaceVisit not found: " + visitId));

        ConsumedItem targetItem = visit.getConsumedItems().stream()
                .filter(item -> itemId.equals(item.getId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ConsumedItem not found: " + itemId));

        String url = saveFile(visitId + "_item_" + itemId + "_" + fileName, fileBytes);

        List<String> photoUrls = targetItem.getPhotoUrls() != null ? new ArrayList<>(targetItem.getPhotoUrls()) : new ArrayList<>();
        photoUrls.add(url);
        targetItem.setPhotoUrls(photoUrls);
        placeVisitRepository.save(visit);

        return url;
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private void computePlaceSummary(String placeId) {
        List<PlaceVisit> visits = placeVisitRepository.findByPlaceIdOrderByVisitedAtDesc(placeId);
        if (visits.isEmpty()) return;

        Place place = placeRepository.findById(placeId).orElseThrow();

        place.setVisitCount(visits.size());

        double avgAmbient = visits.stream()
                .filter(v -> v.getOwnerRating() != null)
                .mapToDouble(v -> v.getOwnerRating().getAmbientRating())
                .average().orElse(0);
        double avgFood = visits.stream()
                .filter(v -> v.getOwnerRating() != null)
                .mapToDouble(v -> v.getOwnerRating().getFoodRating())
                .average().orElse(0);
        double avgPrice = visits.stream()
                .filter(v -> v.getOwnerRating() != null)
                .mapToDouble(v -> v.getOwnerRating().getPriceRating())
                .average().orElse(0);
        place.setAverageRating(new Rating(
                Math.round(avgAmbient),
                Math.round(avgFood),
                Math.round(avgPrice),
                Math.round(avgAmbient) + Math.round(avgFood) + Math.round(avgPrice)
        ));

        PlaceVisit latest = visits.get(0);
        place.setRating(latest.getOwnerRating());
        place.setVisitedAt(latest.getVisitedAt());
        place.setLatestVisitedAt(latest.getVisitedAt());

        placeRepository.save(place);
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

        // Include the place creator if they have no UserVisit
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

    private List<CoVisitor> buildCoVisitorsFromUserVisits(List<UserVisit> userVisits) {
        return userVisits.stream()
                .map(uv -> {
                    var user = userRepository.findById(uv.getUserId()).orElse(null);
                    if (user == null) return null;
                    return CoVisitor.builder()
                            .userId(user.getId())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .profileImageUrl(user.getProfileImageUrl())
                            .rating(uv.getRating())
                            .build();
                })
                .filter(cv -> cv != null)
                .collect(Collectors.toList());
    }

    private String saveFile(String fileName, byte[] fileBytes) throws IOException {
        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        Files.write(uploadDir.resolve(fileName), fileBytes);
        return "/uploads/" + fileName;
    }
}
