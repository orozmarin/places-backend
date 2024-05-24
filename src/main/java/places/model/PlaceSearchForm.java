package places.model;

import java.io.Serializable;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class PlaceSearchForm implements Serializable {

    // Getters and setters
    private PlaceSorting sortingMethod;

}
