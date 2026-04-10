package places.model;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumedItem {

    private String id;
    private String name;
    private ItemCategory category;
    private String notes;
    @Builder.Default
    private List<String> photoUrls = new ArrayList<>();
}
