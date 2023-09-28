package autocancel.app.elasticsearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import autocancel.manager.MainManager;
import autocancel.utils.ReleasableLock;
import autocancel.utils.id.CancellableID;
import autocancel.utils.logger.Logger;

public class TaskTracker {

    private MainManager mainManager;

    private ConcurrentMap<Runnable, CancellableID> queueCancellable;

    private Log log;

    public TaskTracker(MainManager mainManager) {
        this.mainManager = mainManager;

        this.queueCancellable = new ConcurrentHashMap<Runnable, CancellableID>();

        this.log = new Log(this.mainManager);
    }

    public void stop() {
        this.log.stop();
    }

    public void onTaskCreate(Object task, Boolean isCancellable) throws AssertionError {        
        TaskWrapper wrappedTask = new TaskWrapper(task);

        CancellableID parentCancellableID = wrappedTask.getParentTaskID();

        if (parentCancellableID != null) {
            CancellableID cid = this.mainManager.createCancellableIDOnCurrentJavaThreadID(true, 
            task.toString(), 
            wrappedTask.getAction(), 
            parentCancellableID, 
            wrappedTask.getStartTimeNano(),
            wrappedTask.getStartTime());

            Logger.systemTrace("Created " + task.toString());
        }
    }

    public void onTaskExit(Object task) throws AssertionError {
        TaskWrapper wrappedTask = new TaskWrapper(task);
        CancellableID cid = wrappedTask.getTaskID();

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
}
