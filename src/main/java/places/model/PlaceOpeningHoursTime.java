package places.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class PlaceOpeningHoursTime {
    private Integer day;
    private String time;

    public PlaceOpeningHoursTime() {
    }

}
