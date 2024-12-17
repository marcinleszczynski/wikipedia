package put.ir.query.api.wikipedia.fetch;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static put.ir.query.api.wikipedia.WikipediaApiParams.*;

@Slf4j
public class WikipediaFetchService {

    public WikipediaFetchResponse process(WikipediaFetchRequest request) {
        var okHttpClient = new OkHttpClient();

        var url = Objects.requireNonNull(HttpUrl.parse(BASE_URL)).newBuilder()
                .addQueryParameter(ACTION, QUERY)
                .addQueryParameter(LIST, RANDOM)
                .addQueryParameter(RN_NAMESPACE, "0")
                .addQueryParameter(RN_LIMIT, String.valueOf(request.getNumberOfDocuments()))
                .addQueryParameter(FORMAT, JSON)
                .build();

        var okHttpRequest = new Request.Builder().url(url).build();

        try (var response = okHttpClient.newCall(okHttpRequest).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                var body = response.body().string();
                return new WikipediaFetchResponse(200, null, map(body));
            }
            log.info("Error in wikipedia request: {}", response.message());
            return new WikipediaFetchResponse(500, response.message(), null);
        } catch (IOException e) {
            log.info("Error in wikipedia request: {}", e.getMessage());
            return new WikipediaFetchResponse(500, e.getMessage(), null);
        }
    }

    @SneakyThrows
    private List<WikipediaFetchResponse.ArticleLinkDto> map(String body) {
        var objectMapper = new ObjectMapper();
        var root = objectMapper.readTree(body);
        root = root.path("query");
        root = root.path("random");
        return objectMapper.treeToValue(root,
                objectMapper.getTypeFactory().constructCollectionType(List.class, WikipediaFetchResponse.ArticleLinkDto.class));
    }
}
