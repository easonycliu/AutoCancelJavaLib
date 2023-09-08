package autocancel.utils.resource;

import java.util.Map;

import autocancel.utils.logger.Logger;

public abstract class MemoryResource extends Resource {

    public Long usingMemory;

    public Long totalMemory;

    public MemoryResource(Boolean global) {
        super(ResourceType.MEMORY, ResourceName.MEMORY, global);

        this.totalMemory = 0L;
        this.usingMemory = 0L;
    }

    public MemoryResource(ResourceName resourceName, Boolean global) {
        super(ResourceType.MEMORY, resourceName, global);

        this.totalMemory = 0L;
        this.usingMemory = 0L;
    }

    @Override
    public Double getResourceUsage() {
        Double resourceUsage = 0.0;
        if (this.totalMemory != 0L) {
            resourceUsage = Double.valueOf(this.totalMemory) / this.totalMemory;
        }
        return resourceUsage;
    }

    // Memory resource update info has keys:
    // total_memory
    // using_memory
    @Override
    public void setResourceUpdateInfo(Map<String, Object> resourceUpdateInfo) {
        for (Map.Entry<String, Object> entry : resourceUpdateInfo.entrySet()) {
            switch (entry.getKey()) {
                case "total_memory":
                    this.totalMemory = (Long) entry.getValue();
                    break;
                case "using_memory":
                    this.usingMemory += (Long) entry.getValue();
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
        this.totalMemory = 0L;
        this.usingMemory = 0L;
    }

    @Override
    public String toString() {
        return String.format("Resource Type: %s, name: %s, total memory: %d, using memory: %d",
                this.getResourceType().toString(),
                this.getResourceName().toString(),
                this.totalMemory,
                this.usingMemory);
    }
}
