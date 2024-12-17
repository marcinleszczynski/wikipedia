package put.ir.query.service;

import put.ir.preprocessor.BagOfWordsService;
import put.ir.preprocessor.CleaningService;
import put.ir.preprocessor.LemmaService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryService {

    public Map<String, Integer> getBagOfWordsFromSinglePage(String url) {
        var scrapingService = new ScrapingService();
        var article = scrapingService.scrapeArticle(url);

        var cleaningService = new CleaningService();
        var words = cleaningService.cleanText(article.getText());
        var stemService = new LemmaService();
        words = stemService.lemmaWords(words);

        var bagOfWordsService = new BagOfWordsService();
        return bagOfWordsService.singleBagOfWords(words);
    }

    public Map<String, Integer> getBagOfWordsFromManyPages(List<String> urls) {
        var result = new HashMap<String, Integer>();

        urls.forEach(url -> {
            var bagOfWords = getBagOfWordsFromSinglePage(url);
            bagOfWords.keySet().forEach(key -> result.put(key, result.containsKey(key) ? result.get(key) + 1 : 1));
        });

        return result;
    }
}
