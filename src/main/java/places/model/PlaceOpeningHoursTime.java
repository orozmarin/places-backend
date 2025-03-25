package places.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class PlaceOpeningHoursTime {
    private Integer day;
    private Integer hours;
    private Integer minutes;
    private String time;
    private Integer nextDate;

    public PlaceOpeningHoursTime() {
    }

}
