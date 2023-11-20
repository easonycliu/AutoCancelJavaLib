package autocancel.core.policy;

import java.io.FileWriter;
import java.nio.file.Paths;

import autocancel.utils.Settings;

public class CancelLogger {
    
    private static final String rootPath = (String) Settings.getSetting("path_to_logs");

    private static final FileWriter writer;

    private static Integer experimentTime = 0;

    static {
        FileWriter tmpWriter = null;
        try {
            tmpWriter = new FileWriter(String.format("%s.log", Paths.get(CancelLogger.rootPath, System.getProperty("experiment.mode")).toString()), true);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        finally {
            writer = tmpWriter;
        }
    }

    public static void addExperimentTime() {
        CancelLogger.experimentTime += 1;
    }

    public static void logExperimentHeader() {
        try {
            CancelLogger.writer.append(String.format("%s,%s,%s,%s\n", "Times", "Throughput", "Cancel", "Recover"));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void logExperimentInfo(Double throughput, Boolean cancel, Boolean recover) {
        try {
            CancelLogger.writer.append(String.format("%d,%f,%b,%b\n", CancelLogger.experimentTime, throughput, cancel, recover));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
