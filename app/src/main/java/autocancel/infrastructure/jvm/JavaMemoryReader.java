package autocancel.infrastructure.jvm;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.Map;

import autocancel.infrastructure.ResourceReader;
import autocancel.utils.id.ID;
import autocancel.utils.id.JavaThreadID;
import autocancel.utils.logger.Logger;

public class JavaMemoryReader extends ResourceReader {

    private com.sun.management.ThreadMXBean sunThreadMXBean;

    private Long totalMemory;

    public JavaMemoryReader() {
        java.lang.management.ThreadMXBean javaThreadMXBean = ManagementFactory.getThreadMXBean();
        if (javaThreadMXBean instanceof com.sun.management.ThreadMXBean) {
            this.sunThreadMXBean = (com.sun.management.ThreadMXBean) javaThreadMXBean;
            if (this.sunThreadMXBean.isThreadAllocatedMemorySupported()) {
                if (!this.sunThreadMXBean.isThreadAllocatedMemoryEnabled()) {
                    this.sunThreadMXBean.setThreadAllocatedMemoryEnabled(true);
                }
            }
            else {
                Logger.systemWarn("Unsupported method getThreadAllocatedBytes() in class com.sun.management.ThreadMXBean");
            }
        }
        else {
            this.sunThreadMXBean = null;
            Logger.systemWarn("Unsupported class com.sun.management.ThreadMXBean");
        }
        this.totalMemory = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax();
    }

    @Override
    public Map<String, Object> readResource(ID id, Integer version) {
        Long totalMemory = 0L;
        Long usingMemory = 0L;
        if (this.sunThreadMXBean != null) {
            totalMemory = this.totalMemory;
            usingMemory = this.sunThreadMXBean.getThreadAllocatedBytes(((JavaThreadID) id).unwrap());
        }
        return Map.of("total_memory", totalMemory, "using_memory", usingMemory);
    }
}
