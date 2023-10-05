package autocancel.utils.resource;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Map;

public class JVMHeapResource extends MemoryResource {

    private static List<GarbageCollectorMXBean> gcMXBeans = ManagementFactory.getGarbageCollectorMXBeans();

    private Long startGCTime;

    private Long currentGCTime;
    
    public JVMHeapResource() {
        super();
        this.startGCTime = JVMHeapResource.getTotalGCTime();
        this.currentGCTime = this.startGCTime;
    }

    @Override
    public Double getSlowdown(Map<String, Object> slowdownInfo) {
        Double slowdown = 0.0;
        Long startTime = (Long) slowdownInfo.get("start_time");
        if (startTime != null) {
            slowdown = Double.valueOf(this.currentGCTime - this.startGCTime) / (System.currentTimeMillis() - startTime);
        }
        return slowdown;
    }

    public static Long getTotalGCTime() {
        Long totalGCTime = 0L;
        for (GarbageCollectorMXBean gcMXBean : JVMHeapResource.gcMXBeans) {
            totalGCTime += gcMXBean.getCollectionTime();
        }
        return totalGCTime;
    }

    @Override
    public void refresh(Map<String, Object> refreshInfo) {
        this.currentGCTime = (Long) refreshInfo.getOrDefault("current_gc_time", 0L);
    }

    @Override
    public String toString() {
        return super.toString() + String.format(", startGCTime %d", this.startGCTime);
    }
}
