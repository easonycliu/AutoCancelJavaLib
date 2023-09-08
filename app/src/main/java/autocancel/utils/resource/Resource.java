package autocancel.utils.resource;

import java.util.Arrays;
import java.util.Map;

import autocancel.utils.Settings;
import autocancel.utils.logger.Logger;

import java.util.List;

public abstract class Resource {

    protected final ResourceType resourceType;

    protected final ResourceName resourceName;

    protected static final List<String> acceptedInfoKeywords = Arrays.asList(
            "wait_time", "occupy_time");

    protected final Boolean global;

    public Resource(ResourceType resourceType, ResourceName resourceName, Boolean global) {
        this.resourceType = resourceType;
        this.resourceName = resourceName;
        this.global = global;
    }

    public abstract Double getSlowdown();

    public abstract Double getContentionLevel();

    public abstract Double getResourceUsage();

    public ResourceName getResourceName() {
        return this.resourceName;
    }

    public ResourceType getResourceType() {
        return this.resourceType;
    }

    final public static Resource createResource(ResourceType type, ResourceName name, Boolean global) {
        Resource resource = null;
        switch (type) {
            case CPU:
                resource = new CPUResource(name, global);
                break;
            case MEMORY:
                if (name.equals(ResourceName.MEMORY)) {
                    if ((String)((Map<?, ?>)Settings.getSetting("monitor_physical_resources")).get("MEMORY") == "JVM") {
                        resource = new JVMHeapResource(global);
                    }
                    else {
                        resource = new EvictableMemoryResource(global);
                    }
                }
                else {
                    resource = new EvictableMemoryResource(name, global);
                }
                break;
            case QUEUE:
                resource = new QueueResource(name, global);
                break;
            case NULL:
            default:
                Logger.systemWarn("Invalid resource type " + type + " when creating resource");
        }
        return resource;
    }

    public abstract void setResourceUpdateInfo(Map<String, Object> resourceUpdateInfo);

    public abstract void reset();

    public abstract String toString();
}
