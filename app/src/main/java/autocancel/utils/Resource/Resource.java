package autocancel.utils.Resource;

import java.util.Map;

public abstract class Resource {
    
    private ResourceType type;

    public Resource(ResourceType type) {
        this.type = type;
    }

    public abstract Double getContentionLevel();

    public ResourceType getResourceType() {
        return this.type;
    }

    public abstract void setContentionInfo(Map<String, Object> contentionInfo);
}
