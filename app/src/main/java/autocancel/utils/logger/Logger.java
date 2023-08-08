package autocancel.utils.logger;

import java.io.Closeable;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

// TODO: maybe we can connect to log4j (application related)
public class Logger implements Closeable {
    
    String rootPath;

    String fileBaseName;

    Integer maxLine;

    FileWriter writer;

    public Logger(String rootPath, String fileBaseName, Integer maxLine) {
        this.rootPath = rootPath;
        this.fileBaseName = fileBaseName;
        this.maxLine = maxLine;
        try {
            this.writer = new FileWriter(String.format("%s%s.log", Paths.get(this.rootPath, this.fileBaseName).toString(), this.getCurrentTimeString()), false);
        }
        catch (Exception e) {
            this.writer = null;
        }
    }

    public void log(String line) {
        if (this.writer != null) {
            try {
                this.writer.write(line);
            }
            catch (Exception e) {

            }
        }
    }

    @Override
    public void close() {
        if (this.writer != null) {
            try {
                this.writer.close();
            }
            catch (Exception e) {

            }
        }
    }

    private String getCurrentTimeString() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd-HH-mm-ss");
        return dateFormat.format(date);
    }
}
