package autocancel.core.monitor;

import autocancel.core.monitor.Monitor;
import autocancel.core.utils.Cancellable;
import autocancel.core.utils.CancellableGroup;
import autocancel.core.utils.OperationRequest;
import autocancel.core.monitor.CPUMonitor;
import autocancel.core.monitor.MemoryMonitor;
import autocancel.manager.MainManager;
import autocancel.utils.Resource.ResourceType;
import autocancel.utils.id.CancellableID;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class MainMonitor {
    
    private Queue<OperationRequest> monitorUpdateToCoreBuffer;

    private final Map<CancellableID, Cancellable> cancellables;

    private final Map<CancellableID, CancellableGroup> rootCancellableToCancellableGroup;

    private final MainManager mainManager;

    private Map<ResourceType, Monitor> monitors;

    public MainMonitor(MainManager mainManager, Map<CancellableID, Cancellable> cancellables, Map<CancellableID, CancellableGroup> rootCancellableToCancellableGroup) {
        this.monitorUpdateToCoreBuffer = new LinkedList<OperationRequest>();
        this.mainManager = mainManager;
        this.cancellables = cancellables;
        this.rootCancellableToCancellableGroup = rootCancellableToCancellableGroup;

        this.monitors = new HashMap<ResourceType, Monitor>();
        this.monitors.put(ResourceType.CPU, new CPUMonitor(this.mainManager));
        this.monitors.put(ResourceType.MEMORY, new MemoryMonitor(this.mainManager));

    }

    public void updateTasksResources() {
        this.mainManager.startNewVersion();
        for (Cancellable cancellable : cancellables.values()) {
            assert this.rootCancellableToCancellableGroup.containsKey(cancellable.getRootID()) : String.format("Ungrouped cancellable %d", cancellable.getID());
            for (ResourceType resourceType : rootCancellableToCancellableGroup.get(cancellable.getRootID()).getResourceTypes()) {
                this.monitorUpdateToCoreBuffer.add(this.monitors.get(resourceType).updateResource(cancellable.getID()));
            }
        }
    }

    // Objects in autocancel core is owned by a single thread currently
    // So we do not need a lock, at least now
    public OperationRequest getMonitorUpdateToCoreWithoutLock() {
        OperationRequest request;
        request = this.monitorUpdateToCoreBuffer.poll();
        return request;
    }

    public Integer getMonitorUpdateToCoreBufferSizeWithoutLock() {
        Integer size;
        size = this.monitorUpdateToCoreBuffer.size();
        return size;
    }
}
