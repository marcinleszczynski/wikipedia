package put.ir.preprocessor;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class LemmaService {

    public List<String> lemmaWords(List<String> words) {
        return lemmaWord(words);
    }

    @SneakyThrows
    private List<String> lemmaWord(List<String> words) {
        try (
            var posModelInputStream = getClass().getClassLoader().getResourceAsStream("en-pos.bin");
            var lemmaModelInputStream = getClass().getClassLoader().getResourceAsStream("en-lemma.bin");
        ) {
            var posModel = new POSModel(posModelInputStream);
            var lemmaModel = new LemmatizerModel(lemmaModelInputStream);

            var posTagger = new POSTaggerME(posModel);
            var lemmatizer = new LemmatizerME(lemmaModel);

            var wordArray = words.toArray(String[]::new);
            var tag = posTagger.tag(wordArray);
            var lemma = lemmatizer.lemmatize(wordArray, tag);

            return Arrays.stream(lemma).toList();
        }
    }
}
