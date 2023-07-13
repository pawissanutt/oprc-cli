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
        },
        description = "The CLI of Oparaca platform (aka OaaS). Before using it, you should set environment varible 'CDS_URL' to the URL of the content delivery service. \n(ex. export CDS_URL=\"http://cds.127.0.0.1.nip.io\")"
)
public class EntryCommand {

}
