package autocancel.core.policy;

import java.util.Map;

import autocancel.utils.Policy;
import autocancel.utils.id.CancellableID;
import autocancel.utils.resource.ResourceName;

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

    static private Double calculateWeightedSum(Map<ResourceName, Double> weight, Map<ResourceName, Double> resourceUsages) {
        Double sum = 0.0;
        for (Map.Entry<ResourceName, Double> usageEntry : resourceUsages.entrySet()) {
            try {
                sum += Math.max(usageEntry.getValue() * weight.get(usageEntry.getKey()), 0.0);
            }
            catch (NullPointerException e) {
                throw new AssertionError(String.format("%s name is not in weight map", usageEntry.getKey()));
            }
        }
        return sum;
    }
}
