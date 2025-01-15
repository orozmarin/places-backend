package places.model;

import java.time.LocalDateTime;
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
    private Rating firstRating;
    private Rating secondRating;

    @Getter
    private double placeRating;
    private LocalDateTime visitedAt;


    public Place() {
    }

    public Place(String name, String address, String city, Integer postalCode, String country,
            Rating firstRating, Rating secondRating, LocalDateTime visitedAt) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
        this.firstRating = firstRating;
        this.secondRating = secondRating;
        this.visitedAt = visitedAt;
    }

}

