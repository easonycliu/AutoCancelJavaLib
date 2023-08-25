package autocancel.utils.Resource;

import java.util.Map;

public abstract class Resource {

    private ResourceName type;

    public Resource(ResourceName type) {
        this.type = type;
    }

    public abstract Double getContentionLevel();

    public ResourceName getResourceName() {
        return this.type;
    }

    public abstract void setContentionInfo(Map<String, Object> contentionInfo);
}
