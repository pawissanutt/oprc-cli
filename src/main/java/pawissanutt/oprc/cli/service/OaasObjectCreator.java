package pawissanutt.oprc.cli.service;

import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import io.vertx.mutiny.ext.web.client.predicate.ResponsePredicate;
import io.vertx.mutiny.uritemplate.UriTemplate;
import io.vertx.mutiny.uritemplate.Variables;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pawissanutt.oprc.cli.mixin.OaasMixin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@ApplicationScoped
public class OaasObjectCreator {
    private static final Logger logger = LoggerFactory.getLogger(OaasObjectCreator.class);
    @Inject
    WebClient webClient;
    @Inject
    Vertx vertx;

    OaasMixin oaasMixin;

    public void setOaasMixin(OaasMixin oaasMixin) {
        this.oaasMixin = oaasMixin;
    }

    public Stream<JsonObject> createObjects(String cls,
                                            JsonObject data,
                                            Map<String, File> files,
                                            int count) {
        return IntStream.range(0, count)
                .mapToObj(__ -> createObject(cls, data, files));
    }


    public JsonObject createObject(String cls,
                                   JsonObject data,
                                   Map<String, File> files) {
        var cds = oaasMixin.getCdsUrl();
        var body = JsonObject.of(
                "cls", cls,
                "data", data
        );
        if (files!=null) {
            body.put("keys", files.keySet());
        }
        logger.debug("submitting {}", body);
        var res = webClient.postAbs(UriTemplate.of("{+cds}/api/object-construct")
                        .expandToString(Variables.variables()
                                .set("cds", cds)))
                .sendJsonObject(body)
                .await().indefinitely();
        if (res.statusCode()!=200) {
            logger.error("Can not create object: code={} body={}", res.statusCode(), res.bodyAsString());
            throw new RuntimeException("Can not create object");
        }
        var resBody = res.bodyAsJsonObject();
        logger.debug("create object: {}", resBody);
        if (files!=null) {
            uploadFiles(files, resBody);
        }
        return resBody;
    }

    public JsonObject createObject2(String cls,
                                    JsonObject data,
                                    String fb,
                                    Map<String, File> files) {
        var invUrl = oaasMixin.getInvUrl();
        var constructBody = JsonObject.of("data", data);
        if (files!=null) {
            constructBody.put("keys", files.keySet());
        }
        var body = JsonObject.of(
                "cls", cls,
                "fb", fb,
                "body", constructBody
        );
        logger.debug("submitting {}", body);
        var res = webClient.postAbs(UriTemplate.of("{+invoker}/oal")
                        .expandToString(Variables.variables()
                                .set("invoker", invUrl)))
                .sendJsonObject(body)
                .await().indefinitely();

        if (res.statusCode()!=200) {
            logger.error("Can not create object: code={} body={}", res.statusCode(), res.bodyAsString());
            throw new RuntimeException("Can not create object");
        }
        var resBody = res.bodyAsJsonObject();
        logger.debug("create object: {}", resBody);
        if (files!=null) {
            uploadFiles(files, resBody);
        }
        return resBody;
    }

    void uploadFiles(Map<String, File> files,
                     JsonObject constructRes) {
        var urls = constructRes.getJsonObject("uploadUrls").getMap();
        for (var entry : urls.entrySet()) {
            uploadFile(entry.getKey(), files.get(entry.getKey()), (String) entry.getValue());
        }
    }

    void uploadFile(String key, File file, String url) {
        logger.info("uploading file {}={} to {}", key, file.getPath(), url);
        try {
            String mimeType = Files.probeContentType(file.toPath());
            var asyncFile = vertx.fileSystem().openAndAwait(file.getPath(), new OpenOptions());
            var size = asyncFile.size().await().indefinitely();
            webClient.putAbs(url)
                    .putHeader("content-type", mimeType)
                    .putHeader("content-length", size.toString())
                    .expect(ResponsePredicate.SC_SUCCESS)
                    .sendStreamAndAwait(asyncFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
