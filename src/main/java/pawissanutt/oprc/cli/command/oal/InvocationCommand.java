package pawissanutt.oprc.cli.command.oal;

import io.vertx.mutiny.ext.web.client.WebClient;
import io.vertx.mutiny.uritemplate.UriTemplate;
import io.vertx.mutiny.uritemplate.Variables;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pawissanutt.oprc.cli.mixin.OaasMixin;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "invoke",
        aliases = {"inv", "i"},
        mixinStandardHelpOptions = true
)
public class InvocationCommand implements Callable<Integer> {
    private static final Logger logger = LoggerFactory.getLogger(InvocationCommand.class);
    @CommandLine.Mixin
    OaasMixin oaasMixin;
    @CommandLine.Parameters()
    String oal;
    @Inject
    WebClient webClient;

    @CommandLine.Option(names = {"-a", "--async"}, defaultValue = "false")
    boolean async;

    @Override
    public Integer call() throws Exception {
        var req = webClient.getAbs(UriTemplate.of("{+cds}/oal/{+oal}")
                .expandToString(Variables.variables()
                        .set("cds", oaasMixin.getCdsUrl())
                        .set("oal", oal)
                ));
        req.queryParams()
                .set("async", String.valueOf(async));
        var res = req
                .sendAndAwait();
        if (res.statusCode() >= 400) {
            logger.error("error response: {} {}", res.statusCode(),
                    res.bodyAsString());
            return 1;
        }
        if (oal.contains("/")) {
            System.out.println(res.bodyAsBuffer());
        } else {
            System.out.println(res.bodyAsJsonObject().encodePrettily());
        }
        return 0;
    }
}
