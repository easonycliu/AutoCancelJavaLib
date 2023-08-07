package autocancel.infrastructure.linux;

import autocancel.infrastructure.AbstractInfrastructure;
import autocancel.infrastructure.ResourceBatch;
import autocancel.infrastructure.ResourceReader;
import autocancel.utils.Resource.ResourceType;
import autocancel.utils.id.CancellableID;
import autocancel.utils.id.ID;
import autocancel.utils.id.JavaThreadID;
import autocancel.infrastructure.linux.LinuxThreadID;

import sun.jvm.hotspot.debugger.Address;
import sun.jvm.hotspot.oops.Field;
import sun.jvm.hotspot.oops.InstanceKlass;
import sun.jvm.hotspot.oops.Klass;
import sun.jvm.hotspot.oops.LongField;
import sun.jvm.hotspot.oops.Oop;
import sun.jvm.hotspot.runtime.JavaThread;
import sun.jvm.hotspot.runtime.Threads;
import sun.jvm.hotspot.runtime.VM;

import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LinuxThreadStatusReader extends AbstractInfrastructure {
    
    private Map<JavaThreadID, LinuxThreadID> javaThreadIDToLinuxThreadID;

    private List<ResourceType> resourceTypes;

    private Map<ResourceType, ResourceReader> resourceReaders;

    public LinuxThreadStatusReader() {
        super();

        this.javaThreadIDToLinuxThreadID = new HashMap<JavaThreadID, LinuxThreadID>();
        
        this.resourceTypes = this.getRequiredResourceTypes();

        this.resourceReaders = this.initializeResourceReaders();
    }

    public Map<ResourceType, ResourceReader> initializeResourceReaders() {
        Map<ResourceType, ResourceReader> resourceReaders = new HashMap<ResourceType, ResourceReader>();
        resourceReaders.put(ResourceType.CPU, new LinuxCPUReader());
        resourceReaders.put(ResourceType.MEMORY, new LinuxMemoryReader());

        return resourceReaders;
    }

    public List<ResourceType> getRequiredResourceTypes() {
        // TODO: set which resource types to update by settings
        return new ArrayList<ResourceType>(Arrays.asList(ResourceType.CPU, ResourceType.MEMORY));
    }

    @Override
    protected void updateResource(ID id, Integer version) {
        LinuxThreadID linuxThreadID = this.getLinuxThreadIDFromJavaThreadID((JavaThreadID) id);
        assert !linuxThreadID.equals(new LinuxThreadID()) : "Failed to find linux thread id of java thread id";
        
        ResourceBatch resourceBatch = new ResourceBatch(version);
        for (ResourceType type : this.resourceTypes) {
            Double value = this.resourceReaders.get(type).readResource(id, type);
            resourceBatch.setResourceValue(type, value);
        }

        this.setResourceBatch(linuxThreadID, resourceBatch);
    }

    private LinuxThreadID getLinuxThreadIDFromJavaThreadID(JavaThreadID jid) {
        LinuxThreadID linuxThreadID = null;
        if (this.javaThreadIDToLinuxThreadID.containsKey(jid)) {
            linuxThreadID = this.javaThreadIDToLinuxThreadID.get(jid);
        }
        else {
            Threads threads = VM.getVM().getThreads();
            for (JavaThread thread = threads.first(); thread != null; thread = thread.next()) {
                if (getJavaThreadID(thread).equals(jid)) {
                    linuxThreadID = new LinuxThreadID(Long.parseLong(thread.getThreadProxy().toString()));
                    this.javaThreadIDToLinuxThreadID.put(jid, linuxThreadID);
                }
            }
            if (linuxThreadID == null) {
                linuxThreadID = new LinuxThreadID();
            }
        }
        return linuxThreadID;
    }

    public static JavaThreadID getJavaThreadID(JavaThread thread) {
        final JavaThreadID BAD_TID = new JavaThreadID();
        
        Oop threadObj = thread.getThreadObj();
        Klass klass = threadObj.getKlass();
        if (!(klass instanceof InstanceKlass)) return BAD_TID;
        
        InstanceKlass instanceKlass = (InstanceKlass) klass;
        Field tidField = instanceKlass.findField("tid", "J");
        if (!(tidField instanceof LongField)) return BAD_TID;
        
        long tid = ((LongField) tidField).getValue(threadObj);
        return new JavaThreadID(tid);

    }

}