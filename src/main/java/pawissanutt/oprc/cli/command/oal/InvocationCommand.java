package pawissanutt.oprc.cli.command.oal;

import io.vertx.mutiny.ext.web.client.WebClient;
import io.vertx.mutiny.uritemplate.UriTemplate;
import io.vertx.mutiny.uritemplate.Variables;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pawissanutt.oprc.cli.mixin.CommonOutputMixin;
import pawissanutt.oprc.cli.mixin.OaasMixin;
import pawissanutt.oprc.cli.service.OutputFormatter;
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
    @CommandLine.Mixin
    CommonOutputMixin commonOutputMixin;
    @CommandLine.Parameters()
    String oal;
    @Inject
    OutputFormatter outputFormatter;
    @Inject
    WebClient webClient;

    @CommandLine.Option(names = {"-a", "--async"}, defaultValue = "false")
    boolean async;

    @Override
    public Integer call() throws Exception {
        var url = UriTemplate.of("{+inv}/oal/{+oal}")
                .expandToString(Variables.variables()
                        .set("inv", oaasMixin.getInvUrl())
                        .set("oal", oal)
                );
        logger.debug("request to url [{}]", url);
        var req = webClient.getAbs(url);
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
            outputFormatter.print(commonOutputMixin.getOutputFormat(), res.bodyAsJsonObject());
        }
        return 0;
    }
}
