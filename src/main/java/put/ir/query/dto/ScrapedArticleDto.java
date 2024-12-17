package put.ir.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScrapedArticleDto {

    private String title;
    private String text;
    private String url;
}
