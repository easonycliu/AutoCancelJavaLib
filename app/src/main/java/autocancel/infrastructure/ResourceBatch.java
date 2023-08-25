package autocancel.infrastructure;

import autocancel.core.utils.ResourceUsage;
import autocancel.utils.Resource.ResourceName;

import java.util.Map;
import java.util.HashMap;

public class ResourceBatch {

    private Integer version;

    private Map<ResourceName, ResourceUsage> resourceMap;

    public ResourceBatch(Integer version) {
        this.version = version;
        this.resourceMap = new HashMap<ResourceName, ResourceUsage>();
    }

    public void setResourceValue(ResourceName type, Double value) {
        this.resourceMap.put(type, new ResourceUsage(value));
    }

    public Double getResourceValue(ResourceName type) {
        Double resource;
        if (this.resourceMap.containsKey(type)) {
            resource = this.resourceMap.get(type).getUsage();
        } else {
            resource = 0.0;
        }
        return resource;
    }

    public Integer getVersion() {
        return this.version;
    }
}
