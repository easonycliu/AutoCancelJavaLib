package autocancel.utils.logger;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

// TODO: maybe we can connect to log4j (application related)
public class Logger implements Closeable {
    
    String rootPath;

    String fileBaseName;

    Integer maxLine;

    Integer currentLine;

    FileWriter writer;

    public Logger(String rootPath, String fileBaseName, Integer maxLine) {
        this.rootPath = rootPath;
        this.fileBaseName = fileBaseName;
        this.maxLine = maxLine;
        this.currentLine = 0;
        this.writer = this.createFileWriter();
    }

    public void log(String line) {
        this.checkCurrentLine();
        if (this.writer != null) {
            try {
                this.writer.write(line);
                this.currentLine += 1;
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

    private String getCurrentDayString() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
        return dateFormat.format(date);
    }

    private FileWriter createFileWriter() {
        FileWriter fileWriter;
        try {
            String currentDay = this.getCurrentDayString();
            Path directory = Paths.get(this.rootPath, currentDay);
            if (Files.notExists(directory)) {
                Files.createDirectory(directory);
            }
            
            fileWriter = new FileWriter(String.format("%s%s.log", Paths.get(directory.toString(), this.fileBaseName).toString(), this.getCurrentTimeString()), true);
        }
        catch (Exception e) {
            fileWriter = null;
        }
        return fileWriter;
    }

    private void checkCurrentLine() {
        if (this.writer != null) {
            if (this.currentLine >= this.maxLine) {
                try {
                    this.writer.close();
                }
                catch (Exception e) {

                }
                this.currentLine = 0;
                this.writer = this.createFileWriter();
            }
        }
    }
}
