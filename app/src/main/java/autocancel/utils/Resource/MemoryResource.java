package autocancel.utils.Resource;

import java.util.Map;

public class MemoryResource extends Resource {

    public MemoryResource() {
        super(ResourceType.MEMORY, ResourceName.MEMORY);
    }

    public MemoryResource(ResourceName resourceName) {
        super(ResourceType.MEMORY, resourceName);
    }

    @Override
    public Double getContentionLevel() {
        return 0.0;
    }

    @Override
    public void setContentionInfo(Map<String, Object> contentionInfo) {

    }
}
