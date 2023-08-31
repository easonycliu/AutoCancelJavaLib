package autocancel.core;

import java.util.Map;

import autocancel.core.performance.Performance;
import autocancel.core.utils.Cancellable;
import autocancel.core.utils.CancellableGroup;
import autocancel.core.utils.ResourcePool;
import autocancel.utils.id.CancellableID;

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
}
