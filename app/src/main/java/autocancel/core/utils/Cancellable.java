package autocancel.core.utils;

import autocancel.core.utils.ResourceUsage;
import autocancel.utils.Resource.ResourceType;
import autocancel.utils.id.CancellableID;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Cancellable {
    
    private CancellableID id;

    private CancellableID parentID;

    private CancellableID rootID;

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
}
