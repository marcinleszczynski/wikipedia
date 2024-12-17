package put.ir.query.api.wikipedia.fetch;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WikipediaFetchRequest {
    private int numberOfDocuments;
}
