package places.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaceSearchForm implements Serializable {
    private PlaceSorting sortingMethod;
}
