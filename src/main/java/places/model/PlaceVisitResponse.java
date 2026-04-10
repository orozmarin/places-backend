package places.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceVisitResponse {

    private PlaceVisit visit;
    private List<CoVisitor> coVisitors;
}
