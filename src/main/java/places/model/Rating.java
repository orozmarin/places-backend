package places.model;

import lombok.Data;

@Data
public class Rating {
    private final double ambientRating;
    private final double foodRating;
    private final double priceRating;
    private final double placeRating;

    public Rating(double ambientRating, double foodRating, double priceRating, double placeRating) {
        this.ambientRating = ambientRating;
        this.foodRating = foodRating;
        this.priceRating = priceRating;
        this.placeRating = ambientRating + foodRating + priceRating;
    }
}
