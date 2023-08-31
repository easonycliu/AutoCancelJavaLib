package autocancel.core.policy;

import autocancel.utils.Policy;
import autocancel.utils.id.CancellableID;

public class BasePolicy extends Policy {
    
    public BasePolicy() {
        super();
    }

    @Override
    public Boolean needCancellation() {
        return false;
    }

    @Override
    public CancellableID getCancelTarget() {
        return new CancellableID();
    }
}
