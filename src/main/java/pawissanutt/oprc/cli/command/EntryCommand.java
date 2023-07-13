package pawissanutt.oprc.cli.command;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import pawissanutt.oprc.cli.command.cls.ClsEntryCommand;
import pawissanutt.oprc.cli.command.fn.FnEntryCommand;
import pawissanutt.oprc.cli.command.oal.InvocationCommand;
import pawissanutt.oprc.cli.command.obj.ObjectEntryCommand;
import pawissanutt.oprc.cli.command.pkg.PackageEntryCommand;
import picocli.CommandLine.Command;

@TopCommand
@Command(
        mixinStandardHelpOptions = true,
        subcommands = {
                ObjectEntryCommand.class,
                ClsEntryCommand.class,
                FnEntryCommand.class,
                InvocationCommand.class,
                PackageEntryCommand.class
        }
)
public class EntryCommand {

}
