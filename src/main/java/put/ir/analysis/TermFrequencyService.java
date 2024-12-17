package put.ir.analysis;

import put.ir.analysis.dto.DocumentDto;

import java.util.*;

public class TermFrequencyService {

    Map<String, Double> idf = null;

    public List<DocumentDto> toTfIdf(List<DocumentDto> docs) {
        var inverseDocumentFrequencies = inverseDocumentFrequencies(docs);
        idf = inverseDocumentFrequencies;
        var result = new ArrayList<DocumentDto>();

        docs.forEach(document -> {
            var tfIdf = new HashMap<String, Double>();
            document.getTermsFrequency().forEach((key, value) -> {
                tfIdf.put(key, value * inverseDocumentFrequencies.get(key));
            });
            result.add(new DocumentDto(document.getTitle(), tfIdf));
        });

        return result;
    }

    public DocumentDto queryToTfIdf(Set<String> query) {
        var result = new HashMap<String, Double>();

        query.forEach(key -> result.put(key, idf.getOrDefault(key, 0.0)));

        return new DocumentDto("Query", result);
    }

    private Map<String, Double> inverseDocumentFrequencies(List<DocumentDto> docs) {
        var result = new HashMap<String, Double>();
        var itemCount = countItems(docs);
        var numberOfDocuments = docs.size();
        itemCount.forEach((key, value) -> {
            result.put(key, Math.log10(1.0 * numberOfDocuments / value));
        });

        return result;
    }

    private Map<String, Integer> countItems(List<DocumentDto> docs) {
        var result = new HashMap<String, Integer>();

        docs.stream()
                .map(DocumentDto::getTermsFrequency)
                .forEach(termFrequency -> {
                    termFrequency.keySet()
                            .forEach(term -> {
                                result.put(term, result.containsKey(term) ? result.get(term) + 1 : 1);
                            });
                });

        return result;
    }
}
