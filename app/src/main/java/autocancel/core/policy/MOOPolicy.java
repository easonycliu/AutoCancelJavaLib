package autocancel.core.policy;

import autocancel.utils.Policy;
import autocancel.utils.id.CancellableID;

public class MOOPolicy extends Policy {
    
    private CancelTrigger trigger;

    public MOOPolicy() {
        super();
        this.trigger = new CancelTrigger();
    }

    @Override
    public Boolean needCancellation() {
        return this.trigger.triggered(this.infoCenter.getFinishedTaskNumber());
    }

    @Override
    public CancellableID getCancelTarget() {
        return null;
    }
}
