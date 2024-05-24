package places.repository.qdls;

import java.util.List;
import places.model.Place;
import places.model.PlaceSearchForm;

public interface PlaceRepositoryCustom {

    List<Place> findPlacesBySearchForm(PlaceSearchForm placeSearchForm);

}
