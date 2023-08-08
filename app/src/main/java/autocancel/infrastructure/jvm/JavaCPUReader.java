package autocancel.infrastructure.jvm;

import autocancel.infrastructure.ResourceReader;
import autocancel.utils.Resource.ResourceType;
import autocancel.utils.id.ID;
import autocancel.utils.id.JavaThreadID;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;

public class JavaCPUReader extends ResourceReader {

    private Map<JavaThreadID, CPUTimeInfo> javaThreadCPUTime;

    private CPUTimeInfo systemCPUTime;

    private Integer version;

    private ThreadMXBean threadMXBean;

    public JavaCPUReader() {
        super();
        this.javaThreadCPUTime = new HashMap<JavaThreadID, CPUTimeInfo>();
        this.systemCPUTime = new CPUTimeInfo();
        this.version = 0;
        this.threadMXBean = ManagementFactory.getThreadMXBean();
    }

    @Override
    public Double readResource(ID id, Integer version) {
        assert id instanceof JavaThreadID : "Java CPU reader must recieve java thread id";
        if (this.outOfDate(version)) {
            this.refresh(version);
        }
        Double cpuResourceUsage = 0.0;
        if (this.javaThreadCPUTime.containsKey((JavaThreadID) id)) {
            CPUTimeInfo cpuTimeInfo = this.javaThreadCPUTime.get((JavaThreadID) id);
            if (cpuTimeInfo.comparable(this.systemCPUTime)) {
                cpuResourceUsage = Double.valueOf(cpuTimeInfo.diffCPUTime()) / this.systemCPUTime.diffCPUTime();
            }
        }

        return cpuResourceUsage;
    }

    private Boolean outOfDate(Integer version) {
        return !this.version.equals(version);
    }

    private void refresh(Integer version) {
        // update version
        this.version = version;

        // update system cpu time
        this.systemCPUTime.update(version, System.nanoTime());

        // update all working threads
        // if there is a dead threads, do not update it, then its version will not be comparable with system cpu time, thus its utilization will be 0.0.
        long[] threads = this.threadMXBean.getAllThreadIds();
        for (long thread : threads) {
            JavaThreadID jid = new JavaThreadID(thread);
            if (this.javaThreadCPUTime.containsKey(jid)) {
                CPUTimeInfo cpuTimeInfo = this.javaThreadCPUTime.get(jid);
                cpuTimeInfo.update(version, this.threadMXBean.getThreadCpuTime(thread));
            }
            else {
                this.javaThreadCPUTime.put(jid, new CPUTimeInfo(version, this.threadMXBean.getThreadCpuTime(thread)));
            }
        }
    }

    private class CPUTimeInfo {

        private Integer previousVersion;

        private Integer version;

        private Long previousCPUTime;

        private Long cpuTime;

        public CPUTimeInfo() {
            this.previousVersion = 0;
            this.version = 0;
            this.previousCPUTime = 0L;
            this.cpuTime = 0L;
        }

        public CPUTimeInfo(Integer version, Long CPUTime) {
            assert version > 0 && CPUTime > 0L : "version and cpu time should be a positive value.";

            this.previousVersion = 0;
            this.version = version;
            this.previousCPUTime = 0L;
            this.cpuTime = CPUTime;
        }

        public void update(Integer version, Long cpuTime) {
            assert version >= this.version && cpuTime >= this.cpuTime : "version and cpu time should never decrease.";

            this.previousVersion = this.version;
            this.previousCPUTime = this.cpuTime;

            this.version = version;
            this.cpuTime = cpuTime;
        }

        public Integer getPreviousVersion() {
            return this.previousVersion;
        }

        public Integer getVersion() {
            return this.version;
        }

        public Long diffCPUTime() {
            return this.cpuTime - this.previousCPUTime;
        }

        public Boolean comparable(CPUTimeInfo cpuTimeInfo) {
            return this.previousVersion == cpuTimeInfo.getPreviousVersion() && this.version == cpuTimeInfo.getVersion();
        }
    }
}
