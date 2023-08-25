package autocancel.core.monitor;

import java.util.Map;

import autocancel.core.monitor.Monitor;
import autocancel.core.utils.OperationMethod;
import autocancel.core.utils.OperationRequest;
import autocancel.manager.MainManager;
import autocancel.utils.Resource.ResourceName;
import autocancel.utils.id.CancellableID;

public class CPUMonitor implements Monitor {

    MainManager mainManager;

    public CPUMonitor(MainManager mainManager) {
        this.mainManager = mainManager;

    }

    public OperationRequest updateResource(CancellableID cid) {
        OperationRequest request = new OperationRequest(OperationMethod.UPDATE, Map.of("cancellable_id", cid, "resource_name", ResourceName.CPU));
        request.addRequestParam("add_group_resource", this.getResource(cid));
        return request;
    }

    private Double getResource(CancellableID cid) {
        return this.mainManager.getSpecifiedResource(cid, ResourceName.CPU);
    }
}
