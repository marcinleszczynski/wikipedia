package put.ir.preprocessor;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CleaningService {

    private static final Set<String> STOP_WORDS = Set.of(
            "a", "an", "and", "are", "as", "at", "be", "but", "by",
            "for", "if", "in", "into", "is", "it",
            "no", "not", "of", "on", "or", "such",
            "that", "the", "their", "then", "there", "these",
            "they", "this", "to", "was", "will", "with");

    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_+=[{}]|\\\"':;<>,./?–—";

    public List<List<String>> toWords(List<String> texts) {
        return texts.stream()
                .map(this::cleanText)
                .toList();
    }

    public List<String> cleanText(String text) {
        var processedText = removeSpecialCharacters(text);
        processedText = cleanWord(processedText);
        return extractWords(processedText);
    }

    private static String cleanWord(String word) {
        if (word.contains("[")) {
            word = word.substring(0, word.indexOf('['));
        }
        if (word.contains("(")) {
            word = word.substring(word.indexOf('(')+1);
        }
        if (word.contains(")")) {
            word = word.substring(0, word.indexOf(')'));
        }
        return word.toLowerCase().trim();
    }

    private static List<String> extractWords(String text) {
        return Arrays
                .stream(text.split(" "))
                .filter(word -> !STOP_WORDS.contains(word.toLowerCase().trim()))
                .toList();
    }

    private String removeSpecialCharacters(String text) {
        for (char c : SPECIAL_CHARACTERS.toCharArray()) {
            text = text.replace(c, ' ');
        }
        text = text.replace("\\s+", " ");
        return text;
    }
}
