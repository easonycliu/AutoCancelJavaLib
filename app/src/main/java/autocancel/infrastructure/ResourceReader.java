package autocancel.infrastructure;

import autocancel.utils.id.ID;
import autocancel.utils.Resource.ResourceType;

import java.lang.management.ManagementFactory;

public abstract class ResourceReader {
    public abstract Double readResource(ID id, ResourceType type);

    public String getJVMPID() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        return pid;
    }
}
