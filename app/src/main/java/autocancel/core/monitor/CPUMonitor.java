package autocancel.core.monitor;

import autocancel.core.monitor.Monitor;
import autocancel.core.utils.OperationRequest;
import autocancel.manager.MainManager;
import autocancel.utils.id.CancellableID;

public class CPUMonitor implements Monitor {

    MainManager mainManager;

    public CPUMonitor(MainManager mainManager) {
        this.mainManager = mainManager;
        
    }

    public OperationRequest updateResource(CancellableID cid) {
        // TODO: Wrap to a request
        return null;
    }

    private Double getResource(CancellableID cid) {
        return 0.0;
    }
}
