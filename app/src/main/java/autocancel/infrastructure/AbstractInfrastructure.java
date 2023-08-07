package autocancel.infrastructure;

import java.util.HashMap;
import java.util.Map;

import autocancel.utils.Resource.ResourceType;
import autocancel.utils.id.ID;

public abstract class AbstractInfrastructure {
    private Map<ID, ResourceBatch> resourceMap;

    public AbstractInfrastructure() {
        this.resourceMap = new HashMap<ID, ResourceBatch>();
    }

    public Double getResource(ID id, ResourceType type, Integer version) {
        if (this.outOfDate(id, version)) {
            this.updateResource(id, version);
        }
        Double resourceValue = this.getResourceValue(id, type);
        return resourceValue;
    }

    private Boolean outOfDate(ID id, Integer version) {
        Boolean outOfDate;
        if (this.resourceMap.containsKey(id)) {
            if (!this.resourceMap.get(id).getVersion().equals(version)) {
                outOfDate = true;
            }
            else {
                outOfDate = false;
            }
        }
        else {
            outOfDate = true;
        }
        return outOfDate;
    }

    protected abstract void updateResource(ID id, Integer version);

    private Double getResourceValue(ID id, ResourceType type) {
        Double resource;
        if (this.resourceMap.containsKey(id)) {
            resource = this.resourceMap.get(id).getResourceMap(type);
        }
        else {
            resource = 0.0;
        }
        return resource;
    }
}
