package pawissanutt.oprc.cli.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import pawissanutt.oprc.cli.mixin.CommonOutputMixin;

import static pawissanutt.oprc.cli.mixin.CommonOutputMixin.OutputFormat.NDJSON;

@ApplicationScoped
public class OutputFormatter {
    ObjectMapper yamlMapper;

    @Inject
    public OutputFormatter(ObjectMapper mapper) {
        yamlMapper = mapper.copyWith(new YAMLFactory());
    }

    @SneakyThrows
    public void print(CommonOutputMixin.OutputFormat format, JsonObject jsonObject) {
        if (jsonObject.containsKey("items")
                && jsonObject.containsKey("total")
                && format ==NDJSON) {
            print(format, jsonObject.getJsonArray("items"));
            return;
        }
        switch (format) {
            case JSON, NDJSON -> System.out.println(jsonObject);
            case YAML -> System.out.println(yamlMapper.writeValueAsString(jsonObject.getMap()));
            case PJSON -> System.out.println(jsonObject.encodePrettily());
        }
    }

    @SneakyThrows
    public void print(CommonOutputMixin.OutputFormat format, JsonArray jsonArray) {
        switch (format) {
            case JSON -> System.out.println(jsonArray);
            case NDJSON -> {
                for (int i = 0; i < jsonArray.size(); i++) {
                    System.out.println(Json.encode(jsonArray.getList().get(i)));
                }
            }
            case YAML -> System.out.println(yamlMapper.writeValueAsString(jsonArray.getList()));
            case PJSON -> System.out.println(jsonArray.encodePrettily());
        }
    }
}
