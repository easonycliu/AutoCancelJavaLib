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
        this.logger = new Logger("cidinfo");
    }

    public void stop() {
        this.logger.close();
    }
}
