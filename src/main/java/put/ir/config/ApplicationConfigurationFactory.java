package put.ir.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.File;

public class ApplicationConfigurationFactory {

    @SneakyThrows
    public static ApplicationConfiguration fromJson(String pathToJson) {
        var objectMapper = new ObjectMapper();
        var json = new File(pathToJson);
        return objectMapper.readValue(json, ApplicationConfiguration.class);
    }
}
