package autocancel.core;

import autocancel.core.performance.Performance;
import autocancel.core.utils.Cancellable;
import autocancel.core.utils.CancellableGroup;
import autocancel.core.utils.ResourcePool;
import autocancel.core.utils.ResourceUsage;
import autocancel.utils.id.CancellableID;
import autocancel.utils.resource.ResourceName;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class AutoCancelInfoCenter {
    
    private final Map<CancellableID, CancellableGroup> rootCancellableToCancellableGroup;

    private final Map<CancellableID, Cancellable> cancellables;

    private final ResourcePool systemResourcePool;

    private final Performance performanceMetrix;

    public AutoCancelInfoCenter(Map<CancellableID, CancellableGroup> rootCancellableToCancellableGroup,
                                Map<CancellableID, Cancellable> cancellables,
                                ResourcePool systemResourcePool,
                                Performance performanceMetrix) {
        this.rootCancellableToCancellableGroup = rootCancellableToCancellableGroup;
        this.cancellables = cancellables;
        this.systemResourcePool = systemResourcePool;
        this.performanceMetrix = performanceMetrix;
    }

    public Integer getFinishedTaskNumber() {
        return this.performanceMetrix.getFinishedTaskNumber();
    }

    public Double getResourceContentionLevel(ResourceName resourceName) {
        return this.systemResourcePool.getSlowdown(resourceName);
    }

    public Map<ResourceName, Double> getContentionLevel() {
        Set<ResourceName> resourceNames = this.systemResourcePool.getResourceNames();
        Map<ResourceName, Double> resourceContentionLevel = new HashMap<ResourceName, Double>();
        for (ResourceName resourceName : resourceNames) {
            resourceContentionLevel.put(resourceName, this.systemResourcePool.getSlowdown(resourceName));
        }
        return resourceContentionLevel;
    }

    public Map<CancellableID, Double> getCancellableGroupResourceSlowdown(ResourceName resourceName) {
        Map<CancellableID, Double> cancellableGroupSlowdown = new HashMap<CancellableID, Double>();
        for (Map.Entry<CancellableID, CancellableGroup> entry : this.rootCancellableToCancellableGroup.entrySet()) {
            cancellableGroupSlowdown.put(entry.getKey(), entry.getValue().getResourceSlowdown(resourceName));
        }
        return cancellableGroupSlowdown;
    }
}
