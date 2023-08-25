package autocancel.core.monitor;

import autocancel.core.monitor.Monitor;
import autocancel.core.utils.OperationRequest;
import autocancel.core.utils.OperationMethod;
import autocancel.manager.MainManager;
import autocancel.utils.Resource.ResourceName;
import autocancel.utils.id.CancellableID;

public class MemoryMonitor implements Monitor {

    MainManager mainManager;

    public MemoryMonitor(MainManager mainManager) {
        this.mainManager = mainManager;
    }

    public OperationRequest updateResource(CancellableID cid) {
        OperationRequest request = new OperationRequest(OperationMethod.UPDATE, cid, ResourceName.MEMORY);
        request.addRequestParam("add_group_resource", this.getResource(cid));
        return request;
    }

    private Double getResource(CancellableID cid) {
        return this.mainManager.getSpecifiedTypeResource(cid, ResourceName.MEMORY);
    }
}
