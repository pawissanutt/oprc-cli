package pawissanutt.oprc.cli;

import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class ToolProducer {
    @Inject
    Vertx vertx;

    @Produces
    WebClient webClient() {
        return WebClient.create(vertx);
    }
}
