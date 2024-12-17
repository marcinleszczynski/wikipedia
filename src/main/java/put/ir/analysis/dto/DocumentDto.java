package put.ir.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class DocumentDto {

    private String title;
    private Map<String, Double> termsFrequency;
}
