package pawissanutt.oprc.cli.command.obj;

import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import pawissanutt.oprc.cli.mixin.OaasMixin;
import pawissanutt.oprc.cli.service.OaasObjectCreator;
import picocli.CommandLine;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "create",
        aliases = "c",
        mixinStandardHelpOptions = true
)
public class ObjectCreateCommand implements Callable<Integer> {
    @CommandLine.Mixin
    OaasMixin oaasMixin;
    @CommandLine.Parameters(defaultValue = "example.record")
    String cls;

    @CommandLine.Option(names = {"-d", "--data"})
    String data;

    @CommandLine.Option(names = {"-f", "--files"})
    Map<String, File> files;

    @CommandLine.Option(names = "--fb", defaultValue = "new")
    String fb;

    @Inject
    OaasObjectCreator oaasObjectCreator;

    @Override
    public Integer call() throws Exception {
        oaasObjectCreator.setOaasMixin(oaasMixin);
        var res = oaasObjectCreator.createObject2(cls, data!=null ? new JsonObject(data):null, fb, files);
        System.out.println(res);
        return 0;
    }
}
