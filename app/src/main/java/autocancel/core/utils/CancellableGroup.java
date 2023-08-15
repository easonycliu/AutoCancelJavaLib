package autocancel.core.utils;

import autocancel.core.utils.Cancellable;
import autocancel.utils.Resource.ResourceType;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CancellableGroup {
    
    private List<List<Cancellable>> cancellablesInLevels;

    private Map<ResourceType, ResourceUsage> resourceMap;

    private Boolean isCancellable;

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
    
    public CancellableGroup(Cancellable root) {
        this.cancellablesInLevels = new ArrayList<List<Cancellable>>();
        this.cancellablesInLevels.add(new ArrayList<Cancellable>(Arrays.asList(root)));
    }


}
