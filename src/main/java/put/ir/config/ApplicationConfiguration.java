package put.ir.config;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ApplicationConfiguration {

    int numberOfScrapedDocuments;
    boolean shouldScrape;
    boolean shouldPreprocess;
    List<String> readDocuments;
    int maxNumberOfResultPages;
    int numberOfSimilarWords;
}
