package put.ir.query.api.wikipedia.articles;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WikipediaArticlesRequest {

    List<String> ids;
}
