package put.ir.query.service;

import lombok.extern.slf4j.Slf4j;
import put.ir.query.api.wikipedia.articles.WikipediaArticlesRequest;
import put.ir.query.api.wikipedia.articles.WikipediaArticlesResponse;
import put.ir.query.api.wikipedia.articles.WikipediaArticlesService;
import put.ir.query.api.wikipedia.fetch.WikipediaFetchRequest;
import put.ir.query.api.wikipedia.fetch.WikipediaFetchResponse;
import put.ir.query.api.wikipedia.fetch.WikipediaFetchService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
public class WikipediaService {

    public List<String> fetchWikipediaIds(int numberOfDocuments) {

        var result = new ArrayList<String>();
        var iterations = (int) Math.ceil(numberOfDocuments / 500.0);

        IntStream.range(0, iterations)
                .forEach(i -> {
                    var request = new WikipediaFetchRequest(numberOfDocuments);
                    var wikipediaFetchService = new WikipediaFetchService();
                    var response = wikipediaFetchService.process(request);

                    if (response.isSuccess()) {
                        result.addAll(response.getLinks()
                                .stream()
                                .map(WikipediaFetchResponse.ArticleLinkDto::getId)
                                .toList());
                        return;
                    }
                    throw new IllegalStateException("Failed to fetch Wikipedia IDs");
                });

        return result;
    }

    public List<WikipediaArticlesResponse.ArticleDto> fetchWikipediaArticles(List<String> ids) {

        var batchedArticles = batchArticles(ids, 50);
        var result = new ArrayList<WikipediaArticlesResponse.ArticleDto>();

        batchedArticles.forEach(articles -> {
            var request = new WikipediaArticlesRequest(articles);
            var wikipediaArticlesService = new WikipediaArticlesService();
            var response = wikipediaArticlesService.process(request);
            if (response.isSuccess()) {
                result.addAll(response.getArticles());
                return;
            }
            throw new IllegalStateException("Failed to fetch Wikipedia articles: " + response.getErrorMessage());
        });

        return result;
    }

    private List<List<String>> batchArticles(List<String> articles, int limit) {
        var result = new ArrayList<List<String>>();

        int size = articles.size();
        int index = 0;

        while (size > 0) {
            var amount = Math.min(size, limit);
            result.add(articles.subList(index, index + amount));
            index += amount;
            size -= amount;
        }

        return result;
    }
}
