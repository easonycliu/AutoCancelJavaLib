package autocancel.core.monitor;

import autocancel.core.monitor.Monitor;
import autocancel.core.utils.OperationMethod;
import autocancel.core.utils.OperationRequest;
import autocancel.manager.MainManager;
import autocancel.utils.Resource.ResourceType;
import autocancel.utils.id.CancellableID;

public class CPUMonitor implements Monitor {

    MainManager mainManager;

    public CPUMonitor(MainManager mainManager) {
        this.mainManager = mainManager;
        
    }

    public OperationRequest updateResource(CancellableID cid) {
        OperationRequest request = new OperationRequest(OperationMethod.UPDATE, cid, ResourceType.CPU);
        request.addRequestParam("set_group_resource", this.getResource(cid));
        return request;
    }

    private Double getResource(CancellableID cid) {
        return this.mainManager.getSpecifiedTypeResource(cid, ResourceType.CPU);
    }
}
