package autocancel.utils.Resource;

import java.util.Map;

public class MemoryResource extends Resource {
    
    public MemoryResource() {
        super(ResourceType.MEMORY);
    }

    public MemoryResource(ResourceType type) {
        super(type);
    }

    @Override
    public Double getContentionLevel() {
        return 0.0;
    }

    @Override
    public void setContentionInfo(Map<String, Object> contentionInfo) {

    }
}
