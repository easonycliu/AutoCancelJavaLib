package autocancel.utils.Resource;

import java.util.Map;

public class CPUResource extends Resource {
    
    public CPUResource() {
        super(ResourceType.CPU);
    }

    public CPUResource(ResourceType type) {
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
