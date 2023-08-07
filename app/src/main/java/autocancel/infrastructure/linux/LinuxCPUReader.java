package autocancel.infrastructure.linux;

import autocancel.infrastructure.ResourceReader;
import autocancel.utils.Resource.ResourceType;
import autocancel.utils.id.ID;

public class LinuxCPUReader implements ResourceReader {
    
    @Override
    public Double readResource(ID id, ResourceType type) {
        // TODO: Read from /proc/[pid]/task/[tid]/stat
        return 0.0;
    }
}
