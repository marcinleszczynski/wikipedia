package put.ir.preprocessor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import put.ir.analysis.dto.ResultDocumentDto;
import put.ir.query.dto.ScrapedArticleDto;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class CsvService {

    @SneakyThrows
    public void saveScrapedArticles(List<ScrapedArticleDto> articles) {
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
        }
    }

    @SneakyThrows
    public List<ScrapedArticleDto> readScrapedArticles() {
        var format = CSVFormat.DEFAULT.builder()
                .setHeader("title", "text", "url")
                .setSkipHeaderRecord(true)
                .build();

        var reader = new FileReader("scrapedArticles.csv");
        var parser = new CSVParser(reader, format);
        var result = new ArrayList<ScrapedArticleDto>();

        parser.forEach(record -> {
            result.add(new ScrapedArticleDto(record.get("title"), record.get("text"), record.get("url")));
        });

        return result;
    }

    @SneakyThrows
    public void saveLemmatizedArticles(List<ScrapedArticleDto> articles, List<Map<String, Integer>> bagsOfWords) {
        try (var fileWriter = new FileWriter("lemmatizedArticles.csv")) {
            var printer = new CSVPrinter(fileWriter,
                    CSVFormat.DEFAULT.builder()
                            .setHeader("title", "bag_of_words")
                            .build()
            );
            var objectMapper = new ObjectMapper();
            IntStream.range(0, articles.size())
                    .forEach(i -> {
                        try {
                            printer.printRecord(articles.get(i).getTitle(), objectMapper.writeValueAsString(bagsOfWords.get(i)));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    @SneakyThrows
    public List<Map<String, Integer>> readLemmatizedArticles() {
        var format = CSVFormat.DEFAULT.builder()
                .setHeader("title", "bag_of_words")
                .setSkipHeaderRecord(true)
                .build();

        var reader = new FileReader("lemmatizedArticles.csv");
        var parser = new CSVParser(reader, format);
        var result = new ArrayList<Map<String, Integer>>();
        var objectMapper = new ObjectMapper();


        parser.forEach(record -> {
            try {
                result.add(objectMapper.readValue(record.get("bag_of_words"), new TypeReference<>() {}));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        return result;
    }

    @SneakyThrows
    public void saveResult(List<ResultDocumentDto> resultDocuments) {
        try (var fileWriter = new FileWriter("resultOfQuery.csv")) {
            var printer = new CSVPrinter(fileWriter,
                    CSVFormat.DEFAULT.builder()
                            .setHeader("title", "score", "best words")
                            .build()
            );
            resultDocuments.forEach(document -> {
                try {
                    printer.printRecord(document.getTitle(), document.getRating(), document.getMostPopularWords().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
