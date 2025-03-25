package places.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class PlaceReview {
    private String authorName;
    private String authorUrl;
    private String language;
    private String profilePhotoUrl;
    private int rating;
    private String relativeTimeDescription;
    private String text;
    private int time;
}
