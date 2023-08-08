package autocancel.infrastructure.jvm;

import autocancel.infrastructure.AbstractInfrastructure;
import autocancel.utils.Resource.ResourceType;
import autocancel.utils.id.JavaThreadID;
import autocancel.infrastructure.ResourceBatch;
import autocancel.infrastructure.ResourceReader;
import autocancel.utils.id.ID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaThreadStatusReader extends AbstractInfrastructure {

    private List<ResourceType> resourceTypes;

    private Map<ResourceType, ResourceReader> resourceReaders;

    public JavaThreadStatusReader() {
        super();
        
        this.resourceTypes = this.getRequiredResourceTypes();

        this.resourceReaders = this.initializeResourceReaders();
    }

    public Map<ResourceType, ResourceReader> initializeResourceReaders() {
        Map<ResourceType, ResourceReader> resourceReaders = new HashMap<ResourceType, ResourceReader>();
        resourceReaders.put(ResourceType.CPU, new JavaCPUReader());
        resourceReaders.put(ResourceType.MEMORY, new JavaMemoryReader());

        return resourceReaders;
    }

    public List<ResourceType> getRequiredResourceTypes() {
        // TODO: set which resource types to update by settings
        return new ArrayList<ResourceType>(Arrays.asList(ResourceType.CPU, ResourceType.MEMORY));
    }

    @Override
    protected void updateResource(ID id, Integer version) {        
        ResourceBatch resourceBatch = new ResourceBatch(version);
        for (ResourceType type : this.resourceTypes) {
            Double value = this.resourceReaders.get(type).readResource(id, version);
            resourceBatch.setResourceValue(type, value);
        }

        this.setResourceBatch(id, resourceBatch);
    }

}