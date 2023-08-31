package autocancel.utils;

import autocancel.core.AutoCancelCoreHolder;
import autocancel.core.AutoCancelInfoCenter;
import autocancel.utils.id.CancellableID;

public abstract class Policy {
    
    protected final AutoCancelInfoCenter infoCenter;

    public Policy() {
        this.infoCenter = AutoCancelCoreHolder.getInfoCenter();
    }

    public abstract Boolean needCancellation();

    public abstract CancellableID getCancelTarget();

}
