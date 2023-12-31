package pawissanutt.oprc.cli.command.pkg;

import io.vertx.mutiny.core.buffer.Buffer;
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

import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "apply",
        aliases = "a",
        mixinStandardHelpOptions = true
)
public class PackageApplyCommand implements Callable<Integer> {
    private static final Logger logger = LoggerFactory.getLogger(PackageApplyCommand.class);
    @CommandLine.Mixin
    OaasMixin oaasMixin;
    @CommandLine.Parameters()
    File pkgFile;

    @CommandLine.Mixin
    CommonOutputMixin commonOutputMixin;
    @Inject
    OutputFormatter outputFormatter;
    @CommandLine.Option(
            names = {"--override-deploy"},
            defaultValue = "false"
    )
    boolean overrideDeploy;

    @Inject
    WebClient webClient;

    @Override
    public Integer call() throws Exception {

        var pkg = Files.readString(pkgFile.toPath());
        var res = webClient.postAbs(UriTemplate.of("{+oc}/api/packages")
                        .expandToString(Variables.variables()
                                .set("oc", oaasMixin.getOcUrl())))
                .addQueryParam("overrideDeploy", String.valueOf(overrideDeploy))
                .putHeader("content-type", "text/x-yaml")
                .sendBuffer(Buffer.buffer(pkg))
                .await().indefinitely();
        if (res.statusCode()!=200) {
            logger.error("Can not apply package: code={} body={}",
                    res.statusCode(), res.bodyAsString());
            return res.statusCode();
        }

        outputFormatter.print(commonOutputMixin.getOutputFormat(), res.bodyAsJsonObject());
        return 0;
    }
}
