package autocancel.core.monitor;

import autocancel.core.monitor.Monitor;
import autocancel.manager.MainManager;
import autocancel.utils.CancellableID;

public class MemoryMonitor implements Monitor {

    MainManager mainManager;

    public MemoryMonitor(MainManager mainManager) {
        this.mainManager = mainManager;
    }

    public void updateResource(CancellableID cid) {

    }

    private Double getResource(CancellableID cid) {
        return 0.0;
    }
}
