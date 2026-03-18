package places.model;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlaceResponse {

    private String id;
    private String userId;
    private String name;
    private String address;
    private String city;
    private Integer postalCode;
    private String country;
    private String contactNumber;
    private PlaceOpeningHours openingHours;
    private List<Photo> photos;
    private PriceLevel priceLevel;
    private List<PlaceReview> reviews;
    private Double googleRating;
    private String url;
    private String webSiteUrl;
    private Coordinates coordinates;
    private Rating rating;
    private LocalDateTime visitedAt;
    private Boolean isFavorite;
    private List<CoVisitor> coVisitors;
    private String visitId;

    public static PlaceResponse fromPlace(Place place, List<CoVisitor> coVisitors) {
        return PlaceResponse.builder()
                .id(place.getId())
                .userId(place.getUserId())
                .name(place.getName())
                .address(place.getAddress())
                .city(place.getCity())
                .postalCode(place.getPostalCode())
                .country(place.getCountry())
                .contactNumber(place.getContactNumber())
                .openingHours(place.getOpeningHours())
                .photos(place.getPhotos())
                .priceLevel(place.getPriceLevel())
                .reviews(place.getReviews())
                .googleRating(place.getGoogleRating())
                .url(place.getUrl())
                .webSiteUrl(place.getWebSiteUrl())
                .coordinates(place.getCoordinates())
                .rating(place.getRating())
                .visitedAt(place.getVisitedAt())
                .isFavorite(place.getIsFavorite())
                .coVisitors(coVisitors)
                .build();
    }
}
