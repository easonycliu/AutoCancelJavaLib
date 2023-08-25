package autocancel.utils.Resource;

import java.util.Map;
import java.lang.management.ManagementFactory;

public class CPUResource extends Resource {

    public CPUResource() {
        super(ResourceType.CPU, ResourceName.CPU);
    }

    public CPUResource(ResourceName resourceName) {
        super(ResourceType.CPU, resourceName);
    }

    @Override
    public Double getContentionLevel() {
        return ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
    }

    @Override
    public void setContentionInfo(Map<String, Object> contentionInfo) {
        
    }

    @Override
    public void reset() {
        
    }
}
