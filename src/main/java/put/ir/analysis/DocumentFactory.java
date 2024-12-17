package put.ir.analysis;

import put.ir.analysis.dto.DocumentDto;
import put.ir.query.dto.ScrapedArticleDto;

import java.util.*;
import java.util.stream.IntStream;

public class DocumentFactory {

    public static DocumentDto from(ScrapedArticleDto article, Map<String, Integer> bagOfWords) {
        if (bagOfWords.isEmpty()) {
            return null;
        }
        return new DocumentDto(article.getTitle(), normalize(bagOfWords));
    }

    public static List<DocumentDto> from(List<ScrapedArticleDto> articles, List<Map<String, Integer>> bagOfWords) {
        var result = new ArrayList<DocumentDto>();
        IntStream.range(0, articles.size())
                .forEach(i -> result.add(from(articles.get(i), bagOfWords.get(i))));

        return result.stream()
                .filter(Objects::nonNull)
                .toList();
    }

    private static int findHighestValue(Map<String, Integer> bagOfWords) {
        return bagOfWords.values().stream()
                .max(Integer::compareTo)
                .orElseThrow(() -> new IllegalStateException("Bag of words should not be empty"));
    }

    private static Map<String, Double> normalize(Map<String, Integer> bagOfWords) {
        var highestValue = findHighestValue(bagOfWords);
        var result = new HashMap<String, Double>();
        bagOfWords.forEach((key, value) -> result.put(key, 1.0 * value / highestValue));

        return result;
    }
}
