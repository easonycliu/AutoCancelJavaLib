package autocancel.core.utils;

import autocancel.utils.Resource.ResourceType;
import autocancel.utils.logger.Logger;
import autocancel.utils.Resource.Resource;

import java.util.Map;
import java.util.HashMap;

public class ResourcePool {

    private Map<ResourceType, Resource> resources;

    public ResourcePool() {
        this.resources = new HashMap<ResourceType, Resource>();
    }

    public void addResource(Resource resource) {
        if (!this.resources.containsKey(resource.getResourceType())) {
            this.resources.put(resource.getResourceType(), resource);
        }
        else {
            Logger.systemWarn("Resource " + resource.getResourceType().toString() + " has added to resource pool, skip");
        }
    }

    public Double getContentionLevel(ResourceType type) {
        Double contentionLevel = 0.0;
        if (this.resources.containsKey(type)) {
            contentionLevel = this.resources.get(type).getContentionLevel();
        }
        else {
            Logger.systemWarn("Cannot find resource " + type.toString());
        }
        return contentionLevel;
    }

    public void setContentionInfo(ResourceType type, Map<String, Object> contentionInfo) {
        if (this.resources.containsKey(type)) {
            this.resources.get(type).setContentionInfo(contentionInfo);
        }
        else {
            Logger.systemWarn("Cannot find resource " + type.toString());
        }
    }
}
