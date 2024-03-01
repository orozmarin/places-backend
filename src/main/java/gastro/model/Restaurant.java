package gastro.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "restaurants")
@Data
public class Restaurant {
    @Id
    private String id;
    private String name;
    private String address;
    private String city;
    private String country;
    private Rating firstRating;
    private Rating secondRating;
    private double restaurantRating = firstRating.restaurantRating + secondRating.restaurantRating;
}
