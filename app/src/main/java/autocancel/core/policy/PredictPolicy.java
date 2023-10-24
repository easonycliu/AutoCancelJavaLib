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

    public static Map<CancellableID, Double> getCancellableGroupResourceBenefit(ResourceName resourceName) {
        Map<CancellableID, Double> cancellableGroupResourceBenefit = new HashMap<CancellableID, Double>();
        Map<CancellableID, Double> unifiedCancellableGroupResourceUsage = Policy.infoCenter.getUnifiedCancellableGroupResourceUsage(resourceName);
        Map<CancellableID, Long> cancellableGroupRemainTime = Policy.infoCenter.getCancellableGroupRemainTime();
        Set<CancellableID> availableCancellableGroup = new HashSet<CancellableID>(unifiedCancellableGroupResourceUsage.keySet());
        availableCancellableGroup.retainAll(cancellableGroupRemainTime.keySet());
        for (CancellableID cid : availableCancellableGroup) {
            cancellableGroupResourceBenefit.put(cid, unifiedCancellableGroupResourceUsage.get(cid) * cancellableGroupRemainTime.get(cid));
        }
        return cancellableGroupResourceBenefit;
    }

    public static Map<CancellableID, Map<ResourceName, Double>> getCancellableGroupBenefit() {
        Map<CancellableID, Map<ResourceName, Double>> cancellableGroupBenefit = new HashMap<CancellableID, Map<ResourceName, Double>>();
        Map<CancellableID, Map<ResourceName, Double>> unifiedCancellableGroupUsage = Policy.infoCenter.getUnifiedCancellableGroupUsage();
        Map<CancellableID, Long> cancellableGroupRemainTime = Policy.infoCenter.getCancellableGroupRemainTime();
        Set<CancellableID> availableCancellableGroup = new HashSet<CancellableID>(unifiedCancellableGroupUsage.keySet());
        availableCancellableGroup.retainAll(cancellableGroupRemainTime.keySet());
        for (CancellableID cid : availableCancellableGroup) {
            Map<ResourceName, Double> benefit = new HashMap<ResourceName, Double>();
            for (Map.Entry<ResourceName, Double> usageEntry : unifiedCancellableGroupUsage.get(cid).entrySet()) {
                benefit.put(usageEntry.getKey(), usageEntry.getValue() * cancellableGroupRemainTime.get(cid));
            }
            cancellableGroupBenefit.put(cid, benefit);
        }
        return cancellableGroupBenefit;
    }
}
