package put.ir.analysis.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ResultDocumentDto {
    private String title;
    private Double rating;
    private List<String> mostPopularWords;
}
