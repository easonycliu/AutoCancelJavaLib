package autocancel.api;

import autocancel.utils.id.CancellableID;

public class TaskInfo {

    private CancellableID taskID;

    private CancellableID parentID;

    private String action;

    private Long startTimeNano;

    private Long startTime;

    private Boolean isCancellable;

    private String name;

    public TaskInfo(
        Long taskID,
        Long parentID,
        String action,
        Long startTimeNano,
        Long startTime,
        Boolean isCancellable,
        String name
    ) {
        this.taskID = new CancellableID(taskID);
        this.parentID = new CancellableID(parentID);
        this.action = action;
        this.startTimeNano = startTimeNano;
        this.startTime = startTime;
        this.isCancellable = isCancellable;
        this.name = name;
    }

    public CancellableID getParentTaskID() {
        return this.parentID;
    }

    public CancellableID getTaskID() {
        assert this.taskID.isValid() : "Task id should never be invalid";
        return this.taskID;
    }

    public String getAction() {
        return this.action;
    }

    public Long getStartTimeNano() {
        return this.startTimeNano;
    }

    public Long getStartTime() {
        return this.startTime;
    }

    public Boolean getIsCancellable() {
        return this.isCancellable;
    }

    public String getName() {
        return this.name;
    }
}
