package autocancel.utils.Resource;

import java.util.Map;

public class LockResource extends Resource {

    private Integer waitingTasks;

    private Long totalWaitingTime;

    public LockResource(ResourceName type) {
        super(type);
    }

    @Override
    public Double getContentionLevel() {
        return 0.0;
    }

    @Override
    public void setContentionInfo(Map<String, Object> contentionInfo) {

    }
}
