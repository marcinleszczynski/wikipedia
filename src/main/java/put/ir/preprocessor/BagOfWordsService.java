package put.ir.preprocessor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class BagOfWordsService {

    public List<Map<String, Integer>> bagOfWordsOrchestration(List<List<String>> wordList) {
        return wordList.stream()
                .map(this::toBagOfWords)
                .map(this::removeKeys)
                .toList();
    }

    public Map<String, Integer> singleBagOfWords(List<String> words) {
        var bagOfWords = toBagOfWords(words);
        return removeKeys(bagOfWords);
    }

    private Map<String, Integer> toBagOfWords(List<String> words) {
        var result = new HashMap<String, Integer>();
        words.forEach(word -> result.put(word, result.containsKey(word) ? result.get(word) + 1 : 1));
        return result;
    }

    private Map<String, Integer> removeKeys(Map<String, Integer> words) {
        var keySet = new HashSet<>(words.keySet());
        keySet.forEach(key -> {
            if (key.isBlank() || key.matches(".*\\d+.*")) {
                words.remove(key);
            }
        });
        return words;
    }
}
