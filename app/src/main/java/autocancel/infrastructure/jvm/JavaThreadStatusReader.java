package autocancel.infrastructure.jvm;

import autocancel.infrastructure.AbstractInfrastructure;
import autocancel.utils.Settings;
import autocancel.utils.Resource.ResourceName;
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

    private List<ResourceName> resourceTypes;

    private Map<ResourceName, ResourceReader> resourceReaders;

    public JavaThreadStatusReader() {
        super();

        this.resourceTypes = this.getRequiredResourceNames();

        this.resourceReaders = this.initializeResourceReaders();
    }

    public Map<ResourceName, ResourceReader> initializeResourceReaders() {
        Map<ResourceName, ResourceReader> resourceReaders = new HashMap<ResourceName, ResourceReader>();
        resourceReaders.put(ResourceName.CPU, new JavaCPUReader());
        resourceReaders.put(ResourceName.MEMORY, new JavaMemoryReader());

        return resourceReaders;
    }

    private List<ResourceName> getRequiredResourceNames() {
        Map<?, ?> monitorResources = (Map<?, ?>) Settings.getSetting("monitor_physical_resources");
        List<ResourceName> requiredResources = new ArrayList<ResourceName>();
        for (Map.Entry<?, ?> entries : monitorResources.entrySet()) {
            if (((String) entries.getValue()).equals("JVM")) {
                requiredResources.add(ResourceName.valueOf((String) entries.getKey()));
            }
        }
        return requiredResources;
    }

    @Override
    protected void updateResource(ID id, Integer version) {
        ResourceBatch resourceBatch = new ResourceBatch(version);
        for (ResourceName type : this.resourceTypes) {
            Double value = this.resourceReaders.get(type).readResource(id, version);
            resourceBatch.setResourceValue(type, value);
        }

        this.setResourceBatch(id, resourceBatch);
    }

}