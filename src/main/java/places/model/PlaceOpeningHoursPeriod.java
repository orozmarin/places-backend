package places.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class PlaceOpeningHoursPeriod {
    private PlaceOpeningHoursTime open;
    private PlaceOpeningHoursTime close;

    public PlaceOpeningHoursPeriod() {
    }
}
