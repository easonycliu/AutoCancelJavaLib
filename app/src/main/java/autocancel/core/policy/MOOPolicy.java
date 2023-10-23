package autocancel.core.policy;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
        return this.trigger.triggered(Policy.infoCenter.getFinishedTaskNumber());
    }

    @Override
    public CancellableID getCancelTarget() {
        Map<CancellableID, Map<ResourceName, Long>> cancellableGroupUsage = Policy.infoCenter.getCancellableGroupUsage();
        Map<CancellableID, Map<ResourceName, Double>> unifiedCancellableGroupUsage = MOOPolicy.calculateUnifiedCancellableGroupUsage(cancellableGroupUsage);
        Map<ResourceName, Double> weight = Policy.infoCenter.getContentionLevel();
        Map<CancellableID, Double> weightedSum = new HashMap<CancellableID, Double>();
        for (Map.Entry<CancellableID, Map<ResourceName, Double>> cancellableGroupUsageEntry : unifiedCancellableGroupUsage.entrySet()) {
            weightedSum.put(cancellableGroupUsageEntry.getKey(), MOOPolicy.calculateWeightedSum(weight, cancellableGroupUsageEntry.getValue()));
        }
        Map.Entry<CancellableID, Double> maxWeightedSum = weightedSum
                                                            .entrySet()
                                                            .stream()
                                                            .max(Map.Entry.comparingByValue())
                                                            .orElse(null);
        CancellableID target = null;
        if (maxWeightedSum != null) {
            target = maxWeightedSum.getKey();
        }

        if (target == null) {
            System.out.println("Failed to find a target to cancel for unknown reason");
            target = new CancellableID();
        }
        else if (!Policy.infoCenter.isCancellable(target)) {
            System.out.println(target.toString() + " is not cancellable");
            target = new CancellableID();
        }

        return target;
    }

    private static Map<CancellableID, Map<ResourceName, Double>> calculateUnifiedCancellableGroupUsage(Map<CancellableID, Map<ResourceName, Long>> cancellableGroupUsage) {
        Map<ResourceName, Long> cancellableGroupUsageSum = cancellableGroupUsage.values().stream().reduce(new HashMap<ResourceName, Long>(), (result, element) -> {
            element.forEach((key, value) -> {
                result.merge(key, value, Long::sum);
            });
            return result;
        });
        Map<CancellableID, Map<ResourceName, Double>> unifiedCancellableGroupUsage = new HashMap<CancellableID, Map<ResourceName, Double>>();
        for (Map.Entry<CancellableID, Map<ResourceName, Long>> cancellableGroupUsageEntry : cancellableGroupUsage.entrySet()) {
            unifiedCancellableGroupUsage.put(cancellableGroupUsageEntry.getKey(), cancellableGroupUsageEntry.getValue().entrySet().stream().collect(Collectors.toMap(
                element -> element.getKey(),
                element -> Double.valueOf(element.getValue()) / cancellableGroupUsageSum.get(element.getKey())
            )));
        }
        return unifiedCancellableGroupUsage;
    }

    private static Double calculateWeightedSum(Map<ResourceName, Double> weight, Map<ResourceName, Double> resourceUsages) {
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
