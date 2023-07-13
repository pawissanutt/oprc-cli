package pawissanutt.oprc.cli;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TextCollector extends OutputStream {
    private final List<String> lines = new CopyOnWriteArrayList<>();
    private StringBuilder buffer = new StringBuilder();

    PrintStream originStream;

    public TextCollector(PrintStream originStream) {
        this.originStream = originStream;
    }

    @Override
    public void write(int b) throws IOException {
        if (b == '\n') {
            lines.add(buffer.toString());
            buffer = new StringBuilder();
        } else {
            buffer.append((char) b);
        }
        originStream.write(b);
    }

    public List<String> getLines() {
        return lines;
    }
}
