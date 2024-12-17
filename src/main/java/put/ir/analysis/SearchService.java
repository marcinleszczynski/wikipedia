package put.ir.analysis;

import lombok.extern.slf4j.Slf4j;
import put.ir.analysis.dto.DocumentDto;
import put.ir.analysis.dto.ResultDocumentDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
public class SearchService {

    public List<ResultDocumentDto> searchForSimilarDocuments(List<DocumentDto> documents, DocumentDto query, int maxNumberOfSimilarPages, int maxNumberOfSimilarWords) {
        maxNumberOfSimilarPages = Math.min(maxNumberOfSimilarPages, documents.size());
        var sortableDocuments = new ArrayList<>(documents);
        sortableDocuments.sort((doc1, doc2) -> (int) Math.signum(cosineSimilarity(doc2, query) - cosineSimilarity(doc1, query)));

        return sortableDocuments.subList(0, maxNumberOfSimilarPages).stream()
                .map(document -> ResultDocumentDto.builder()
                        .title(document.getTitle())
                        .rating(cosineSimilarity(document, query))
                        .mostPopularWords(findMostPopularWords(document, query, maxNumberOfSimilarWords))
                        .build())
                .toList();
    }

    private double vectorLength(List<Double> terms) {
        var result = terms.stream()
                .reduce(0d, (term1, term2) -> term1 + term2 * term2);
        return Math.sqrt(result);
    }

    public Double cosineSimilarity(DocumentDto doc1, DocumentDto doc2) {
        var keys = new HashSet<>(doc1.getTermsFrequency().keySet());
        keys.addAll(doc2.getTermsFrequency().keySet());

        var keyList = new ArrayList<>(keys);

        var dotProduct = keyList.stream().mapToDouble(s -> doc1.getTermsFrequency().getOrDefault(s, 0.0) * doc2.getTermsFrequency().getOrDefault(s, 0.0))
                .sum();
        return dotProduct / (vectorLength(doc1.getTermsFrequency().values().stream().toList()) * vectorLength(doc2.getTermsFrequency().values().stream().toList()));
    }

    public double evaluateImportance(Double valueFromDocument, Double valueFromQuery) {
        return valueFromDocument * valueFromQuery;
    }

    private List<String> findMostPopularWords(DocumentDto document, DocumentDto query, int maxNumberOfSimilarWords) {
        var words = new ArrayList<>(
                query.getTermsFrequency().keySet().stream().toList()
        );
        words.sort((word1, word2) -> (int) Math.signum(
                evaluateImportance(document.getTermsFrequency().getOrDefault(word2, 0.0), query.getTermsFrequency().getOrDefault(word2, 0.0)) -
                evaluateImportance(document.getTermsFrequency().getOrDefault(word1, 0.0), query.getTermsFrequency().getOrDefault(word1, 0.0))
        ));
        return maxNumberOfSimilarWords > words.size() ? words : words.subList(0, maxNumberOfSimilarWords);
    }
}
