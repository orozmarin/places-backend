package gastro.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "ratings")
@Data
public class Rating {
    private final double ambientRating;
    private final double foodRating;
    private final double priceRating;
    public final double restaurantRating;

    public Rating(double ambientRating, double foodRating, double priceRating) {
        this.ambientRating = ambientRating;
        this.foodRating = foodRating;
        this.priceRating = priceRating;
        this.restaurantRating = ambientRating + foodRating + priceRating;
    }
}
