package places.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "place_visits")
public class PlaceVisit {

    @Id
    private String id;
    private String placeId;
    private String userId;
    private LocalDateTime visitedAt;
    private Rating ownerRating;
    @Builder.Default
    private List<ConsumedItem> consumedItems = new ArrayList<>();
    @Builder.Default
    private List<String> photoUrls = new ArrayList<>();
}
