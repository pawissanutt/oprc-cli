package pawissanutt.oprc.cli.command.cls;

import io.vertx.mutiny.ext.web.client.WebClient;
import io.vertx.mutiny.uritemplate.UriTemplate;
import io.vertx.mutiny.uritemplate.Variables;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pawissanutt.oprc.cli.mixin.CommonOutputMixin;
import pawissanutt.oprc.cli.mixin.OaasMixin;
import pawissanutt.oprc.cli.service.OutputFormatter;
import pawissanutt.oprc.cli.service.WebRequester;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(
        name = "list classes",
        aliases = {"l"},
        mixinStandardHelpOptions = true
)
public class ClsListCommand implements Callable<Integer> {
    private static final Logger logger = LoggerFactory.getLogger( ClsListCommand.class );
    @CommandLine.Mixin
    OaasMixin oaasMixin;
    @CommandLine.Mixin
    CommonOutputMixin commonOutputMixin;
    @Inject
    WebRequester webRequester;

    @CommandLine.Parameters(defaultValue = "")
    String cls;

    @Override
    public Integer call() throws Exception {
        var oc = oaasMixin.getOcUrl();
        return webRequester.getAndPrint(
                UriTemplate.of("{+oc}/api/classes/{+cls}")
                        .expandToString(Variables.variables()
                                .set("oc", oc)
                                .set("cls",cls)
                        ),
                commonOutputMixin.getOutputFormat()
        );
    }
}
