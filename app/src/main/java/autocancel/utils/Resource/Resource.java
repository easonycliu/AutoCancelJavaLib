package autocancel.utils.Resource;

import java.util.Map;

public abstract class Resource {

    private ResourceName resourceName;

    public Resource(ResourceName resourceName) {
        this.resourceName = resourceName;
    }

    public abstract Double getContentionLevel();

    public ResourceName getResourceName() {
        return this.resourceName;
    }

    public abstract void setContentionInfo(Map<String, Object> contentionInfo);
}
