package autocancel.core.monitor;

import autocancel.core.monitor.Monitor;
import autocancel.core.utils.OperationRequest;
import autocancel.manager.MainManager;
import autocancel.utils.Resource.ResourceType;
import autocancel.utils.id.CancellableID;

public class MemoryMonitor implements Monitor {

    MainManager mainManager;

    public MemoryMonitor(MainManager mainManager) {
        this.mainManager = mainManager;
    }

    public OperationRequest updateResource(CancellableID cid) {
        // TODO: Wrap to a request
        return null;
    }

    private Double getResource(CancellableID cid) {
        return this.mainManager.getSpecifiedTypeResource(cid, ResourceType.MEMORY);
    }
}
