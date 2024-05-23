package places.repository.qdls;

import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import places.model.Place;
import places.model.PlaceSearchForm;

@Slf4j
@Component
public class PlaceRepositoryCustomImpl implements PlaceRepositoryCustom{
    private final EntityManager entityManager;

    public PlaceRepositoryCustomImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Place> findPlacesBySearchForm(PlaceSearchForm placeSearchForm) {
        return List.of();
    }
}
