package put.ir.query.api.wikipedia.articles;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static put.ir.query.api.wikipedia.WikipediaApiParams.*;

@Slf4j
public class WikipediaArticlesService {

    public WikipediaArticlesResponse process(WikipediaArticlesRequest request) {

        var okHttpClient = new OkHttpClient();

        var url = Objects.requireNonNull(HttpUrl.parse(BASE_URL)).newBuilder()
                .addQueryParameter(ACTION, QUERY)
                .addQueryParameter(FORMAT, JSON)
                .addQueryParameter(PAGE_IDS, String.join("|", request.getIds()))
                .addQueryParameter(PROP, INFO)
                .addQueryParameter(INPROP, URL)
                .build();

        var okHttpRequest = new Request.Builder()
                .url(url)
                .build();

        try (var response = okHttpClient.newCall(okHttpRequest).execute()) {

            if (response.isSuccessful() && response.body() != null) {
                var body = response.body().string();
                return new WikipediaArticlesResponse(200, null, map(body));
            }
            log.info("Error occurred while fetching wikipedia articles: {}", response.message());
            return new WikipediaArticlesResponse(500, response.message(), null);
        } catch (IOException e) {

            log.info("Error occurred while fetching wikipedia articles: {}", e.getMessage());
            return new WikipediaArticlesResponse(500, e.getMessage(), null);
        }

    }

    @SneakyThrows
    private List<WikipediaArticlesResponse.ArticleDto> map(String body) {
        var objectMapper = new ObjectMapper();
        var root = objectMapper.readTree(body);

        root = root.path(QUERY);
        root = root.path(PAGES);

        var iterator = root.elements();
        var result = new ArrayList<WikipediaArticlesResponse.ArticleDto>();
        iterator.forEachRemaining(node -> result.add(map(node)));

        return result;
    }

    private WikipediaArticlesResponse.ArticleDto map(JsonNode root) {
        return new WikipediaArticlesResponse.ArticleDto(root.get(TITLE).asText(), root.get(FULL_URL).asText());
    }
}
