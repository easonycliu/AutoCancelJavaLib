package autocancel.core;

import autocancel.core.performance.Performance;
import autocancel.core.utils.Cancellable;
import autocancel.core.utils.CancellableGroup;
import autocancel.core.utils.ResourcePool;
import autocancel.core.utils.ResourceUsage;
import autocancel.utils.Resource.ResourceName;
import autocancel.utils.id.CancellableID;

import java.util.Map;
import java.util.HashMap;

public class AutoCancelInfoCenter {
    
    private final Map<CancellableID, CancellableGroup> rootCancellableToCancellableGroup;

    private final Map<CancellableID, Cancellable> cancellables;

    private final ResourcePool resourcePool;

    private final Performance performanceMetrix;

    public AutoCancelInfoCenter(Map<CancellableID, CancellableGroup> rootCancellableToCancellableGroup,
                                Map<CancellableID, Cancellable> cancellables,
                                ResourcePool resourcePool,
                                Performance performanceMetrix) {
        this.rootCancellableToCancellableGroup = rootCancellableToCancellableGroup;
        this.cancellables = cancellables;
        this.resourcePool = resourcePool;
        this.performanceMetrix = performanceMetrix;
    }

    public Integer getFinishedTaskNumber() {
        return this.performanceMetrix.getFinishedTaskNumber();
    }

    public Map<CancellableID, Double> getCancellableCPUUsage() {
        Map<CancellableID, Double> cancellableCPUUSageMap = new HashMap<CancellableID, Double>();
        for (Map.Entry<CancellableID, CancellableGroup> entry : this.rootCancellableToCancellableGroup.entrySet()) {
            if (entry.getValue().getIsCancellable()) {
                cancellableCPUUSageMap.put(entry.getKey(), entry.getValue().getResourceUsage(ResourceName.CPU));
            }
        }
        return cancellableCPUUSageMap;
    }
}
