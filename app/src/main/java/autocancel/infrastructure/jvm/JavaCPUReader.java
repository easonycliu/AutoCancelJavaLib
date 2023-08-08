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

    private Map<JavaThreadID, Long> javaThreadTotalCPUTime;

    private Long systemNanoTime;

    public JavaCPUReader() {
        super();
        this.javaThreadTotalCPUTime = new HashMap<JavaThreadID, Long>();
        this.systemNanoTime = 0L;
    }

    @Override
    public Double readResource(ID id, Integer version) {
        assert id instanceof JavaThreadID : "Java CPU reader must recieve java thread id";
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadInfo threadInfo = threadMXBean.getThreadInfo(((JavaThreadID) id).unwrap());
        long[] threadIDs = threadMXBean.getAllThreadIds();
        return 0.0;
    }
}
