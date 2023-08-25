package autocancel.utils.Resource;

import java.util.Map;

public abstract class Resource {

    private final ResourceType resourceType;

    private final ResourceName resourceName;

    public Resource(ResourceType resourceType, ResourceName resourceName) {
        this.resourceType = resourceType;
        this.resourceName = resourceName;
    }

    public abstract Double getContentionLevel();

    public ResourceName getResourceName() {
        return this.resourceName;
    }

    public ResourceType getResourceTYpe() {
        return this.resourceType;
    }

    public abstract void setContentionInfo(Map<String, Object> contentionInfo);
}
