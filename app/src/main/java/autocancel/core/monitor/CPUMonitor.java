package autocancel.core.monitor;

import autocancel.core.monitor.Monitor;
import autocancel.manager.MainManager;
import autocancel.core.utils.CancellableID;

public class CPUMonitor implements Monitor {

    MainManager mainManager;

    public CPUMonitor(MainManager mainManager) {
        this.mainManager = mainManager;
        
    }

    public void updateResource(CancellableID cid) {
        
    }

    private Double getResource(CancellableID cid) {
        return 0.0;
    }
}
