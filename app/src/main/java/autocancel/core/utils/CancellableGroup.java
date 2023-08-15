package autocancel.core.utils;

import autocancel.core.utils.Cancellable;
import autocancel.utils.Resource.ResourceType;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

public class CancellableGroup {

    private final Cancellable root;
    
    private Set<Cancellable> cancellables;

    private Map<ResourceType, ResourceUsage> resourceMap;

    private Boolean isCancellable;

    public CancellableGroup(Cancellable root) {
        this.root = root;
        this.cancellables = new HashSet<Cancellable>();
        this.cancellables.add(root);
        this.resourceMap = new HashMap<ResourceType, ResourceUsage>();
        
        // These are "built-in" monitored resources
        this.resourceMap.put(ResourceType.CPU, new ResourceUsage());
        this.resourceMap.put(ResourceType.MEMORY, new ResourceUsage());

        this.isCancellable = null;
    }

    public Set<ResourceType> getResourceTypes() {
        return this.resourceMap.keySet();
    }

    public void setResourceUsage(ResourceType type, Double usage) {
        if (this.resourceMap.containsKey(type)) {
            this.resourceMap.get(type).setUsage(usage);
        }
        else {
            this.resourceMap.put(type, new ResourceUsage(usage));
        }
    }

    public Boolean getIsCancellable() {
        assert this.isCancellable != null : "this.isCancellable hasn't been set yet";
        return this.isCancellable;
    }

    public void setIsCancellable(Boolean isCancellable) {
        this.isCancellable = isCancellable;
    }

    public void putCancellable(Cancellable cancellable) {
        assert cancellable.getRootID().equals(this.root.getID()) : 
            String.format("Putting a cancellable with id %d into a wrong group with root cancellable id %d", cancellable.getID(), this.root.getID());

        assert !this.cancellables.contains(cancellable) : 
            String.format("Cancellable %d has been putted into this group %d", cancellable.getID(), this.root.getID());
            
        this.cancellables.add(cancellable);
    }
    
}
