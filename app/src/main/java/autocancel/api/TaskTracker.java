package autocancel.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import autocancel.manager.MainManager;
import autocancel.utils.id.CancellableID;
import autocancel.utils.logger.Logger;

public class TaskTracker {

    private MainManager mainManager;

    private Function<Object, TaskInfo> taskInfoApplier;

    private ConcurrentMap<Runnable, CancellableID> queueCancellable;

    public TaskTracker(MainManager mainManager) {
        this.mainManager = mainManager;

        this.queueCancellable = new ConcurrentHashMap<Runnable, CancellableID>();
    }

    public void stop() {
    }

    public void onTaskCreate(Object task, Boolean isCancellable) throws AssertionError {        
        TaskInfo taskInfo = this.taskInfoApplier.apply(task);

        this.mainManager.createCancellableIDOnCurrentJavaThreadID(
            taskInfo.getTaskID(),
            isCancellable, 
            task.toString(), 
            taskInfo.getAction(), 
            taskInfo.getParentTaskID(), 
            taskInfo.getStartTimeNano(),
            taskInfo.getStartTime()
        );

        Logger.systemTrace("Created " + task.toString());
    }

    public void onTaskExit(Object task) throws AssertionError {
        TaskInfo taskInfo = this.taskInfoApplier.apply(task);
        CancellableID cid = taskInfo.getTaskID();

        Logger.systemTrace("Exit " + task.toString());

        if (cid.isValid()) {
            this.mainManager.destoryCancellableIDOnCurrentJavaThreadID(cid);
        }
        else {
            Logger.systemWarn(String.format("Error parsing %s", task.toString()));
        }
    }

    public void onTaskFinishInThread() throws AssertionError {
        CancellableID cid = this.mainManager.getCancellableIDOnCurrentJavaThreadID();
        if (cid.isValid()) {
            this.mainManager.unregisterCancellableIDOnCurrentJavaThreadID();
        }
        else {
            Logger.systemWarn("Should have a cancellable running on a thread but not found");
        }
    }

    public void onTaskQueueInThread(Runnable runnable) throws AssertionError {
        assert runnable != null : "Runable cannot be a null pointer.";

        CancellableID cid = this.mainManager.getCancellableIDOnCurrentJavaThreadID();

        if (cid.isValid()) {
            assert this.queueCancellable.put(runnable, cid) == null : "Duplicated runnable from threadpool";
        }
        else {
            Logger.systemTrace("Cannot found corresponding cancellable from current thread");
        }
    }

    public void onTaskStartInThread(Runnable runnable) throws AssertionError {
        assert runnable != null : "Runable cannot be a null pointer.";

        CancellableID cid = this.queueCancellable.remove(runnable);

        if (cid != null) {
            this.mainManager.registerCancellableIDOnCurrentJavaThreadID(cid);
        }
        else {
            Logger.systemTrace("Cannot found corresponding cancellable from runnable");
        }
    }

    public void addTaskWork(Long work) {
        this.mainManager.updateCancellableGroupWork(Map.of(
            "add_work", work
        ));
    }

    public void finishTaskWork(Long work) {
        this.mainManager.updateCancellableGroupWork(Map.of(
            "finish_work", work
        ));
    }
}
