package pawissanutt.oprc.cli.command.pkg;

import pawissanutt.oprc.cli.command.obj.ObjectCreateCommand;
import picocli.CommandLine.Command;

@Command(
        name = "package",
        aliases = {"pkg", "p"},
        mixinStandardHelpOptions = true,
        subcommands = {
                PackageApplyCommand.class
        }
)
public class PackageEntryCommand {

}
