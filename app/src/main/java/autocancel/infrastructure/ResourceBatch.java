package autocancel.infrastructure;

import autocancel.utils.Resource.ResourceType;

import java.util.Map;
import java.util.HashMap;

public class ResourceBatch {
    
    private Integer version;

    private Map<ResourceType, Double> resourceMap;

    public ResourceBatch(Integer version) {
        this.version = version;
        this.resourceMap = new HashMap<ResourceType, Double>();
    }

    public void setResourceMap(ResourceType type, Double value) {
        this.resourceMap.put(type, value);
    }

    public Double getResourceMap(ResourceType type) {
        Double resource;
        if (this.resourceMap.containsKey(type)) {
            resource = this.resourceMap.get(type);
        }
        else {
            resource = 0.0;
        }
        return resource;
    }

    public Integer getVersion() {
        return this.version;
    }
}
