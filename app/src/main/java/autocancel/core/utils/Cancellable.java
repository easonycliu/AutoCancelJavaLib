package autocancel.core.utils;

import autocancel.core.utils.ResourceUsage;
import autocancel.utils.Resource.ResourceType;
import autocancel.utils.id.CancellableID;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Cancellable {
    
    private final CancellableID id;

    private final CancellableID parentID;

    private final CancellableID rootID;

    private String name;

    public Cancellable(CancellableID id, CancellableID parentID, CancellableID rootID) {
        this.id = id;
        this.parentID = parentID;
        this.rootID = rootID;
        this.name = "Anonymous";
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CancellableID getID() {
        return this.id;
    }

    public CancellableID getParentID() {
        return this.parentID;
    }

    public CancellableID getRootID() {
        return this.rootID;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Cancellable && ((Cancellable) o).getID().equals(this.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
