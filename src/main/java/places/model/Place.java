package places.model;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@AllArgsConstructor
@Document(collection = "places")
@Data
public class Place {

    @Id
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

    private String ownershipTransferredFromName;
    private LocalDateTime ownershipTransferredAt;

    public Place() {
    }
}
