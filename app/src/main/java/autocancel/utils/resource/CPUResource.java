package autocancel.utils.resource;

import java.util.Map;

import autocancel.utils.logger.Logger;

public class CPUResource extends Resource {

    public Long totalSystemTime;

    public Long usedSystemTime;

    public CPUResource() {
        super(ResourceType.CPU, ResourceName.CPU);
        this.totalSystemTime = 0L;
        this.usedSystemTime = 0L;
    }

    public CPUResource(ResourceName resourceName) {
        super(ResourceType.CPU, resourceName);
        this.totalSystemTime = 0L;
        this.usedSystemTime = 0L;
    }

    @Override
    public Double getSlowdown() {
        return 1.0 - Double.valueOf(this.usedSystemTime) / this.totalSystemTime;
    }

    // CPU resource update info has keys:
    // cpu_time_system
    // cpu_time_thread
    @Override
    public void setResourceUpdateInfo(Map<String, Object> resourceUpdateInfo) {
        for (Map.Entry<String, Object> entry : resourceUpdateInfo.entrySet()) {
            switch (entry.getKey()) {
                case "cpu_time_system":
                    this.totalSystemTime += (Long) entry.getValue();
                    break;
                case "cpu_time_thread":
                    this.usedSystemTime += (Long) entry.getValue();
                    break;
                default:
                    Logger.systemWarn("Invalid info name " + entry.getKey() + " in resource type " + this.resourceType
                            + " ,name " + this.resourceName);
                    break;
            }
        }
    }

    @Override
    public void reset() {
        this.totalSystemTime = 0L;
        this.usedSystemTime = 0L;
    }

    @Override
    public String toString() {
        return String.format("Resource Type: %s, name: %s, total system time: %d, used system time: %d",
                this.getResourceType().toString(),
                this.getResourceName().toString(),
                this.totalSystemTime,
                this.usedSystemTime);
    }
}
