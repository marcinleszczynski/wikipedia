package put.ir.query.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import put.ir.query.api.wikipedia.articles.WikipediaArticlesResponse.ArticleDto;
import put.ir.query.dto.ScrapedArticleDto;
import org.jsoup.Jsoup;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
public class ScrapingService {

    public List<ScrapedArticleDto> scrapeArticles(List<ArticleDto> articles) {
        var result = articles.stream()
                .map(article -> scrapeArticle(article.getFullUrl()))
                .toList();
        return result;
    }

    @SneakyThrows
    public ScrapedArticleDto scrapeArticle(String articleUrl) {
        var doc = Jsoup.connect(articleUrl)
                .get();
        var paragraphs = doc.select("#mw-content-text .mw-content-ltr p");

        var resultText = new StringBuilder();

        paragraphs.forEach(paragraph -> resultText.append(paragraph.text()));

        return new ScrapedArticleDto(doc.title(), resultText.toString(), articleUrl);
    }

    private void saveScrapedArticles(List<ScrapedArticleDto> articles) {
        try (var fileWriter = new FileWriter("scrapedArticles.csv")) {
            var printer = new CSVPrinter(fileWriter,
                    CSVFormat.DEFAULT.builder()
                            .setHeader("title", "text", "url")
                            .build()
            );

            articles.forEach(article -> {
                try {
                    printer.printRecord(article.getTitle(), article.getText(), article.getUrl());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            log.info("Error occured during saving to csv: {}", e.getMessage());
        }
    }
}
