package pawissanutt.oprc.cli.command.cls;

import io.vertx.mutiny.ext.web.client.WebClient;
import io.vertx.mutiny.uritemplate.UriTemplate;
import io.vertx.mutiny.uritemplate.Variables;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pawissanutt.oprc.cli.mixin.OaasMixin;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(
        name = "list",
        aliases = {"l"},
        mixinStandardHelpOptions = true
)
public class ClsListCommand implements Callable<Integer> {
    private static final Logger logger = LoggerFactory.getLogger( ClsListCommand.class );
    @CommandLine.Mixin
    OaasMixin oaasMixin;
    @Inject
    WebClient webClient;

    @Override
    public Integer call() throws Exception {
        var oc = oaasMixin.getOcUrl();
        var res = webClient.getAbs(UriTemplate.of("{+oc}/api/classes")
                .expandToString(Variables.variables()
                        .set("oc", oc)))
                .sendAndAwait();
        if (res.statusCode() != 200){
            logger.error("error response: {} {}", res.statusCode(), res
                    .bodyAsString());
            return 1;
        }
        System.out.println(res.bodyAsJsonObject().encodePrettily());
        return 0;
    }
}
