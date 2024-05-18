package gastro.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "ratings")
@Data
public class Rating {
    private final double ambientRating;
    private final double foodRating;
    private final double priceRating;
    private final double restaurantRating;

    public Rating(double ambientRating, double foodRating, double priceRating, double restaurantRating) {
        this.ambientRating = ambientRating;
        this.foodRating = foodRating;
        this.priceRating = priceRating;
        this.restaurantRating = ambientRating + foodRating + priceRating;
    }
}
