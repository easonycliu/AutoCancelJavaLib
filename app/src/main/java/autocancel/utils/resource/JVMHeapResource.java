package autocancel.utils.resource;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;

public class JVMHeapResource extends MemoryResource {

    private List<GarbageCollectorMXBean> gcMXBeans;

    private Long prevGCTime;

    private Long gcTime;

    private Long prevCPUTime;

    private Long cpuTime;
    
    public JVMHeapResource() {
        super();
        this.gcMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        this.prevGCTime = 0L;
        this.gcTime = this.getTotalGCTime();
        this.prevCPUTime = 0L;
        this.cpuTime = System.currentTimeMillis();
    }

    @Override
    public Double getSlowdown() {
        return Double.valueOf(gcTime - prevGCTime) / (cpuTime - prevCPUTime);
    }

    private Long getTotalGCTime() {
        Long totalGCTime = 0L;
        for (GarbageCollectorMXBean gcMXBean : this.gcMXBeans) {
            totalGCTime += gcMXBean.getCollectionTime();
        }
        return totalGCTime;
    }

    @Override
    public void reset() {
        super.reset();
        this.prevGCTime = this.gcTime;
        this.gcTime = this.getTotalGCTime();
        this.prevCPUTime = this.cpuTime;
        this.cpuTime = System.currentTimeMillis();
    }
}
