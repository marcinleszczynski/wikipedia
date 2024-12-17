package put.ir.query.api.wikipedia.articles;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class WikipediaArticlesResponse {

    private int status;
    private String errorMessage;
    private List<ArticleDto> articles;

    @Data
    @AllArgsConstructor
    public static class ArticleDto {
        private String title;
        private String fullUrl;
    }

    public boolean isSuccess() {
        return status == 200;
    }
}
