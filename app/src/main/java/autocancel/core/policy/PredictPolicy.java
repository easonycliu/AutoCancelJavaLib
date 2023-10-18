package autocancel.core.policy;

import autocancel.utils.Policy;
import autocancel.utils.id.CancellableID;

public class PredictPolicy extends Policy {
    private CancelTrigger trigger;

    public PredictPolicy() {
        super();
        this.trigger = new CancelTrigger();
    }

    @Override
    public Boolean needCancellation() {
        return this.trigger.triggered(Policy.infoCenter.getFinishedTaskNumber());
    }

    @Override
    public CancellableID getCancelTarget() {
        return null;
    }
}
