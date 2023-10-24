package autocancel.core.policy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import autocancel.utils.Policy;
import autocancel.utils.id.CancellableID;
import autocancel.utils.resource.ResourceName;

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
