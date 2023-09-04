package autocancel.core.utils;

import autocancel.utils.Resource.ResourceName;
import autocancel.utils.Resource.ResourceType;
import autocancel.utils.logger.Logger;
import autocancel.utils.Resource.CPUResource;
import autocancel.utils.Resource.MemoryResource;
import autocancel.utils.Resource.QueueResource;
import autocancel.utils.Resource.Resource;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class ResourcePool {

    private Map<ResourceName, Resource> resources;

    public ResourcePool() {
        this.resources = new HashMap<ResourceName, Resource>();
    }

    public void addResource(Resource resource) {
        if (!this.resources.containsKey(resource.getResourceName())) {
            this.resources.put(resource.getResourceName(), resource);
        } else {
            Logger.systemWarn(
                    "Resource " + resource.getResourceName().toString() + " has added to resource pool, skip");
        }
    }

    public void addResource(ResourceType type, ResourceName name) {
        if (!this.resources.containsKey(name)) {
            Resource resource = this.createResource(type, name);
            if (resource != null) {
                this.resources.put(name, resource);
            }
        } else {
            Logger.systemWarn(
                    "Resource " + name + " has added to resource pool, skip");
        }
    }

    public Boolean isResourceExist(ResourceName resourceName) {
        return this.resources.containsKey(resourceName);
    }

    public Double getContentionLevel(ResourceName resourceName) {
        Double contentionLevel = 0.0;
        if (this.resources.containsKey(resourceName)) {
            contentionLevel = this.resources.get(resourceName).getContentionLevel();
        } else {
            Logger.systemWarn("Cannot find resource " + resourceName.toString());
        }
        return contentionLevel;
    }

    public void setResourceUpdateInfo(ResourceName resourceName, Map<String, Object> resourceUpdateInfo) {
        if (this.resources.containsKey(resourceName)) {
            this.resources.get(resourceName).setResourceUpdateInfo(resourceUpdateInfo);
        } else {
            Logger.systemWarn("Cannot find resource " + resourceName.toString());
        }
    }

    public void refreshResources(Logger logger) {
        for (Map.Entry<ResourceName, Resource> entries : this.resources.entrySet()) {
            if (logger != null) {
                logger.log(entries.getValue().toString());
            }
            entries.getValue().reset();
        }
    }

    public Set<ResourceName> getResourceNames() {
        return this.resources.keySet();
    }

    private Resource createResource(ResourceType type, ResourceName name) {
        Resource resource = null;
        switch (type) {
            case CPU:
                resource = new CPUResource(name);
                break;
            case MEMORY:
                resource = new MemoryResource(name);
                break;
            case QUEUE:
                resource = new QueueResource(name);
                break;
            case NULL:
            default:
                Logger.systemWarn("Invalid resource type " + type + " when creating resource");
        }
        return resource;
    }
}
