package autocancel.utils.Resource;

import java.util.Map;

public class CPUResource extends Resource {

    public CPUResource() {
        super(ResourceType.CPU, ResourceName.CPU);
    }

    public CPUResource(ResourceName resourceName) {
        super(ResourceType.CPU, resourceName);
    }

    @Override
    public Double getContentionLevel() {
        return 0.0;
    }

    @Override
    public void setContentionInfo(Map<String, Object> contentionInfo) {

    }
}
