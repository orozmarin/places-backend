package places.model;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@AllArgsConstructor
@Document(collection = "places")
@Data
public class Place {

    @Id
    private String id;
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
    private Rating firstRating;
    private Rating secondRating;

    @Getter
    private double placeRating;
    private LocalDateTime visitedAt;


    public Place() {
    }

    public Place(String name, String address, String city, Integer postalCode, String country, String contactNumber,
            PlaceOpeningHours openingHours, List<Photo> photos, PriceLevel priceLevel, Coordinates coordinates,
            List<PlaceReview> reviews, Double googleRating, String url, String webSiteUrl, Rating firstRating,
            Rating secondRating, LocalDateTime visitedAt) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
        this.contactNumber = contactNumber;
        this.openingHours = openingHours;
        this.photos = photos;
        this.priceLevel = priceLevel;
        this.reviews = reviews;
        this.googleRating = googleRating;
        this.url = url;
        this.webSiteUrl = webSiteUrl;
        this.coordinates = coordinates;
        this.firstRating = firstRating;
        this.secondRating = secondRating;
        this.visitedAt = visitedAt;
    }

}

