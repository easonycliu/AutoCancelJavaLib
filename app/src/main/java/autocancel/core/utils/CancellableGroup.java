package autocancel.core.utils;

import autocancel.core.utils.Cancellable;
import autocancel.utils.Resource.CPUResource;
import autocancel.utils.Resource.MemoryResource;
import autocancel.utils.Resource.Resource;
import autocancel.utils.Resource.ResourceName;
import autocancel.utils.Resource.ResourceType;
import autocancel.utils.id.CancellableID;
import autocancel.utils.Settings;

import java.util.Map;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.List;

public class CancellableGroup {

    private final Cancellable root;

    private Map<CancellableID, Cancellable> cancellables;

    private ResourcePool resourcePool;

    private Boolean isCancellable;

    private Boolean exited;

    public CancellableGroup(Cancellable root) {
        root.setLevel(0);
        this.root = root;

        this.cancellables = new HashMap<CancellableID, Cancellable>();
        this.cancellables.put(root.getID(), root);
        this.resourcePool = new ResourcePool();

        // These are "built-in" monitored resources
        this.resourcePool.addResource(new CPUResource());
        this.resourcePool.addResource(new MemoryResource());

        this.isCancellable = null;

        this.exited = false;
    }

    public void exit() {
        this.exited = true;
    }

    public Boolean isExit() {
        return this.exited;
    }

    public Set<ResourceName> getResourceNames() {
        return this.resourcePool.getResourceNames();
    }

    public void refreshResourcePool() {
        this.resourcePool.refreshResources(null);
    }

    public void updateResource(ResourceType resourceType, ResourceName resourceName, Map<String, Object> resourceUpdateInfo) {
        if (!this.resourcePool.isResourceExist(resourceName)) {
            this.resourcePool.addResource(resourceType, resourceName);
        }
        this.resourcePool.setResourceUpdateInfo(resourceName, resourceUpdateInfo);
    }

    public Double getResourceSlowdown(ResourceName resourceName) {
        return this.resourcePool.getSlowDown(resourceName);
    }

    public Boolean getIsCancellable() {
        assert this.isCancellable != null : "this.isCancellable hasn't been set yet";
        return this.isCancellable;
    }

    public void setIsCancellable(Boolean isCancellable) {
        this.isCancellable = isCancellable;
    }

    public void putCancellable(Cancellable cancellable) {
        assert cancellable.getRootID().equals(this.root.getID())
                : String.format("Putting a cancellable with id %d into a wrong group with root cancellable id %d",
                        cancellable.getID(), this.root.getID());

        assert !this.cancellables.containsKey(cancellable.getID()) : String
                .format("Cancellable %d has been putted into this group %d", cancellable.getID(), this.root.getID());

        Integer level = this.getCancellableLevel(cancellable);
        cancellable.setLevel(level);

        this.cancellables.put(cancellable.getID(), cancellable);
    }

    public Collection<Cancellable> getChildCancellables() {
        return this.cancellables.values();
    }

    private Integer getCancellableLevel(Cancellable cancellable) {
        Integer level = 0;
        CancellableID tmp;
        Integer maxLevel = (Integer) Settings.getSetting("max_child_cancellable_level");
        do {
            tmp = cancellable.getParentID();
            if (!tmp.isValid()) {
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
        } while (!tmp.equals(this.root.getID()));

        return level;
    }
}
