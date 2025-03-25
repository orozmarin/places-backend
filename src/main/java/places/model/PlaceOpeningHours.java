package places.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class PlaceOpeningHours {

    private Boolean openNow;
    private List<PlaceOpeningHoursPeriod> periods;
    private List<String> weekdayText;

    public PlaceOpeningHours() {
    }

}
