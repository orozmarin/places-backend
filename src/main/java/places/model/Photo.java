package places.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class Photo {
    private String photoReference;
    private int height;
    private int width;
    private List<String> htmlAttributions;

    // Constructors
    public Photo() {
    }
}
