package autocancel.app.elasticsearch;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import autocancel.manager.MainManager;
import autocancel.utils.Settings;
import autocancel.utils.id.CancellableID;
import autocancel.utils.id.IDInfo;
import autocancel.utils.id.JavaThreadID;
import autocancel.utils.logger.Logger;

public class Log {
    
    MainManager mainManager;

    Logger logger;

    public Log(MainManager mainManager) {
        this.mainManager = mainManager;
        this.logger = new Logger((String) Settings.getSetting("path_to_logs"), "cidinfo");
    }

    public void stop() {
        this.logger.close();
    }

    public void logCancellableJavaThreadIDInfo(CancellableID cid, Object task) {
        List<IDInfo<JavaThreadID>> javaThreadIDInfos = this.mainManager.getAllJavaThreadIDInfoOfCancellableID(cid);

        this.logger.log(String.format("========== Cancellable %s %s ==========\n", cid.toString(), task.toString()));
        for (IDInfo<JavaThreadID> javaThreadIDInfo : javaThreadIDInfos) {
            this.logger.log(javaThreadIDInfo.toString() + "\n");
        }
    }

}
