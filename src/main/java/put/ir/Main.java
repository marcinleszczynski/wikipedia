package put.ir;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import put.ir.analysis.DocumentFactory;
import put.ir.analysis.SearchService;
import put.ir.analysis.TermFrequencyService;
import put.ir.analysis.dto.DocumentDto;
import put.ir.config.ApplicationConfigurationFactory;
import put.ir.preprocessor.BagOfWordsService;
import put.ir.preprocessor.CleaningService;
import put.ir.preprocessor.CsvService;
import put.ir.preprocessor.LemmaService;
import put.ir.query.dto.ScrapedArticleDto;
import put.ir.query.service.QueryService;
import put.ir.query.service.ScrapingService;
import put.ir.query.service.WikipediaService;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@Slf4j
public class Main {

    @SneakyThrows
    public static void main(String[] args) {

        var config = ApplicationConfigurationFactory.fromJson("config.json");

        var csvService = new CsvService();
        var termsFrequencyService = new TermFrequencyService();

        var numberOfDocuments = config.getNumberOfScrapedDocuments();
        boolean shouldScrape = config.isShouldScrape();
        boolean shouldPreprocess = config.isShouldPreprocess();
        var readDocuments = config.getReadDocuments();
        var maxNumberOfResultPages = config.getMaxNumberOfResultPages();
        var maxNumberOfSimilarWords = config.getNumberOfSimilarWords();

        var data = scrapeStep(numberOfDocuments, csvService, shouldScrape);

        var bagsOfWords = preprocessingStep(data, csvService, shouldPreprocess);

        var inverseFrequencyDocuments = vectorProcessStep(data, bagsOfWords, termsFrequencyService);

        var tfIdfQuery = userQueryStep(readDocuments, termsFrequencyService);

        analysisStep(inverseFrequencyDocuments, tfIdfQuery, csvService, maxNumberOfResultPages, maxNumberOfSimilarWords);
    }

    private static void analysisStep(List<DocumentDto> inverseFrequencyDocuments, DocumentDto tfIdfQuery, CsvService csvService, int maxNumberOfPages, int maxNumberOfSimilarWords) {
        var searchService = new SearchService();
        var queried = searchService.searchForSimilarDocuments(inverseFrequencyDocuments, tfIdfQuery, maxNumberOfPages, maxNumberOfSimilarWords);
        csvService.saveResult(queried);
    }

    private static List<DocumentDto> vectorProcessStep(List<ScrapedArticleDto> data, List<Map<String, Integer>> bagsOfWords, TermFrequencyService termsFrequencyService) {
        var documents = DocumentFactory.from(data, bagsOfWords);
        return termsFrequencyService.toTfIdf(documents);
    }

    private static DocumentDto userQueryStep(List<String> readDocuments, TermFrequencyService termsFrequencyService) {
        var queryService = new QueryService();
        var query = queryService.getBagOfWordsFromManyPages(readDocuments);
        return termsFrequencyService.queryToTfIdf(query.keySet());
    }

    private static List<Map<String, Integer>> preprocessingStep(List<ScrapedArticleDto> data, CsvService csvService, boolean shouldPreprocess) {

        if (shouldPreprocess) {
            var cleaningService = new CleaningService();
            var words = cleaningService.toWords(data.stream().map(ScrapedArticleDto::getText).toList());
            var lemmaService = new LemmaService();
            words = words.stream().map(lemmaService::lemmaWords).toList();
            var bagOfWordsService = new BagOfWordsService();
            var bagsOfWords = bagOfWordsService.bagOfWordsOrchestration(words);
            csvService.saveLemmatizedArticles(data, bagsOfWords);
            return bagsOfWords;
        }
        return csvService.readLemmatizedArticles();
    }

    @NotNull
    private static List<ScrapedArticleDto> scrapeStep(int numberOfDocuments, CsvService csvService, boolean shouldScrape) {

        if (shouldScrape) {
            var wikipediaService = new WikipediaService();
            var ids = wikipediaService.fetchWikipediaIds(numberOfDocuments);
            var articles = wikipediaService.fetchWikipediaArticles(ids);
            var scrapeService = new ScrapingService();
            var data = scrapeService.scrapeArticles(articles);
            csvService.saveScrapedArticles(data);
            return data;
        }
        return csvService.readScrapedArticles();
    }
}