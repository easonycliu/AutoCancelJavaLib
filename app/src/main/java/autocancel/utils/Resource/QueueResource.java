package autocancel.utils.Resource;

import java.util.Map;

public class QueueResource extends Resource {

    private Integer triedTasks;

    private Long totalWaitTime;

    public QueueResource(ResourceName resourceName) {
        super(ResourceType.QUEUE, resourceName);
        this.triedTasks = 0;
        this.totalWaitTime = 0L;
    }

    @Override
    public Double getContentionLevel() {
        return 0.0;
    }

    @Override
    public void setResourceUpdateInfo(Map<String, Object> resourceUpdateInfo) {
        Long waitTime = (Long) resourceUpdateInfo.get("wait_time");
        if (waitTime != null) {
            this.triedTasks += 1;
            this.totalWaitTime += waitTime;
        }
    }

    @Override
    public void reset() {
        this.triedTasks = 0;
        this.totalWaitTime = 0L;
    }

    @Override
    public String toString() {
        return String.format("Resource Type: %s, Name: %s, Tried tasks: %d, Total wait time: %d", 
        this.getResourceType().toString(),
        this.getResourceName().toString(),
        this.triedTasks,
        this.totalWaitTime);
    }
}
