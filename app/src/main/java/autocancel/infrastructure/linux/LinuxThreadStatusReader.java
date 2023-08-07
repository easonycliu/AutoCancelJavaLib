package autocancel.infrastructure.linux;

import autocancel.infrastructure.AbstractInfrastructure;
import autocancel.utils.Resource.ResourceType;
import autocancel.utils.id.CancellableID;
import autocancel.utils.id.ID;
import autocancel.utils.id.JavaThreadID;

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
import java.util.HashMap;

public class LinuxThreadStatusReader extends AbstractInfrastructure {
    
    private Map<JavaThreadID, LinuxThreadID> javaThreadIDToLinuxThreadID;

    public LinuxThreadStatusReader() {
        super();
        this.javaThreadIDToLinuxThreadID = new HashMap<JavaThreadID, LinuxThreadID>();
    }

    @Override
    protected void updateResource(ID id, Integer version) {
        LinuxThreadID linuxThreadID = this.getLinuxThreadIDFromJavaThreadID((JavaThreadID) id);
        assert !linuxThreadID.equals(new LinuxThreadID()) : "Failed to find linux thread id of java thread id";
        // TODO: set which resource types to update by settings
        
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

    private class LinuxThreadID implements ID {
        private Long id;

        public LinuxThreadID(Long id) {
            this.id = id;
        }

        // Invalid LinuxThreadID
        public LinuxThreadID() {
            this.id = -1L;
        }

        @Override
        public String toString() {
            return String.format("Linux Thread ID : %d", this.id);
        }

        @Override
        public boolean equals(Object o) {
            // TODO: Class should be the same
            return this.id == ((LinuxThreadID) o).id;
        }

        @Override
        public int hashCode() {
            return this.id.intValue();
        }

        public Long unwrap() {
            return this.id;
        }
    }
}