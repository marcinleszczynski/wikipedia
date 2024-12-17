package put.ir.query.api.wikipedia.fetch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class WikipediaFetchResponse {

    private int status;
    private String errorMessage;
    private List<ArticleLinkDto> links;

    @Data
    @NoArgsConstructor
    public static class ArticleLinkDto {
        private String title;
        private String ns;
        private String id;
    }

    public boolean isSuccess() {
        return status == 200;
    }
}
