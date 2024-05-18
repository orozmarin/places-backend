package gastro.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@AllArgsConstructor
@Document(collection = "restaurants")
@Data
public class Restaurant {

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
    private double restaurantRating;

    public Restaurant() {
    }

    public Restaurant(String name, String address, String city, Integer postalCode, String country,
            Rating firstRating, Rating secondRating) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
        this.firstRating = firstRating;
        this.secondRating = secondRating;
    }

}

