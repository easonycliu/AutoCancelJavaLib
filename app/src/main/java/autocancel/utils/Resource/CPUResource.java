package autocancel.utils.Resource;

import java.util.Map;

public class CPUResource extends Resource {

    public CPUResource() {
        super(ResourceName.CPU);
    }

    public CPUResource(ResourceName resourceName) {
        super(resourceName);
    }

    @Override
    public Double getContentionLevel() {
        return 0.0;
    }

    @Override
    public void setContentionInfo(Map<String, Object> contentionInfo) {

    }
}
