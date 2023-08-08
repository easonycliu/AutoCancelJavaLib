package autocancel.app.elasticsearch;

import autocancel.manager.MainManager;

public class Log {
    
    MainManager mainManager;

    TaskTracker taskTracker;

    public Log(MainManager mainManager, TaskTracker taskTracker) {
        this.mainManager = mainManager;
        this.taskTracker = taskTracker;
    }

    public void stop() {
        
    }
}
