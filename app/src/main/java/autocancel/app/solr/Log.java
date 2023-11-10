package autocancel.app.solr;

import autocancel.manager.MainManager;
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
