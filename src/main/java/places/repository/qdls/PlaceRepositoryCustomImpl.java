package places.repository.qdls;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Component;
import places.model.Place;
import places.model.PlaceSearchForm;

@Slf4j
@Component
public class PlaceRepositoryCustomImpl implements PlaceRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Place> findPlacesBySearchForm(PlaceSearchForm placeSearchForm) {
        List<Sort.Order> orders = new ArrayList<>();

        switch (placeSearchForm.getSortingMethod()) {
            case ALPHABETICALLY_ASC:
                orders.add(Sort.Order.asc("name"));
                break;
            case ALPHABETICALLY_DESC:
                orders.add(Sort.Order.desc("name"));
                break;
            case RATING_ASC:
                orders.add(Sort.Order.asc("placeRating"));
                break;
            case RATING_DESC:
                orders.add(Sort.Order.desc("placeRating"));
                break;
            case DATE_ASC:
                orders.add(Sort.Order.asc("visitedAt"));
                break;
            case DATE_DESC:
                orders.add(Sort.Order.desc("visitedAt"));
                break;
            default:
                throw new IllegalArgumentException("Invalid sorting method");
        }

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.sort(Sort.by(orders))
        );

        AggregationResults<Place> results = mongoTemplate.aggregate(aggregation, "places", Place.class);
        return results.getMappedResults();
    }
}

