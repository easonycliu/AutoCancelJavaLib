package autocancel.core.utils;

import autocancel.core.utils.Cancellable;
import autocancel.utils.Resource.ResourceType;
import autocancel.utils.id.CancellableID;
import autocancel.utils.Settings;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class CancellableGroup {

    private final Cancellable root;
    
    private Map<CancellableID, Cancellable> cancellables;

    private Map<ResourceType, ResourceUsage> resourceMap;

    private Boolean isCancellable;

    public CancellableGroup(Cancellable root) {
        root.setLevel(0);
        this.root = root;

        this.cancellables = new HashMap<CancellableID, Cancellable>();
        this.cancellables.put(root.getID(), root);
        this.resourceMap = new HashMap<ResourceType, ResourceUsage>();
        
        // These are "built-in" monitored resources
        this.resourceMap.put(ResourceType.CPU, new ResourceUsage());
        this.resourceMap.put(ResourceType.MEMORY, new ResourceUsage());

        this.isCancellable = null;
    }

    // TODO: Add a function addResourceUsage()

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

        assert !this.cancellables.containsKey(cancellable.getID()) : 
            String.format("Cancellable %d has been putted into this group %d", cancellable.getID(), this.root.getID());

        Integer level = this.getCancellableLevel(cancellable);
        cancellable.setLevel(level);
            
        this.cancellables.put(cancellable.getID(), cancellable);
    }

    private Integer getCancellableLevel(Cancellable cancellable) {
        Integer level = 0;
        CancellableID tmp;
        Integer maxLevel = (Integer) Settings.getSetting("max_child_cancellable_level");
        do {
            tmp = cancellable.getParentID();
            if (tmp.equals(new CancellableID())) {
                // In case someone use this function to calculate the level of root cancellable
                break;
            }
            level += 1;
            if (level > maxLevel) {
                // There must something wrong, untrack it
                // TODO: add warning
                level = -1;
                break;
            }
        } while(!tmp.equals(this.root.getID()));

        return level;
    }
    
}
