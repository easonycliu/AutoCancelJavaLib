package autocancel.app.elasticsearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import autocancel.manager.MainManager;
import autocancel.utils.ReleasableLock;
import autocancel.utils.id.CancellableID;

public class TaskTracker {

    private MainManager mainManager;

    private Map<CancellableID, List<Runnable>> cancellableIDToAsyncRunnables;

    private BiMap<CancellableID, TaskWrapper> cancellableIDTaskBiMap;

    private Map<Long, TaskWrapper> tasks;

    private ReadWriteLock autoCancelReadWriteLock;

    private ReleasableLock readLock;

    private ReleasableLock writeLock;

    private Log log;

    public TaskTracker(MainManager mainManager) {
        this.mainManager = mainManager;

        this.cancellableIDToAsyncRunnables = new HashMap<CancellableID, List<Runnable>>();

        this.cancellableIDTaskBiMap = HashBiMap.create();

        this.tasks = new HashMap<Long, TaskWrapper>();

        this.autoCancelReadWriteLock = new ReentrantReadWriteLock();

        this.readLock = new ReleasableLock(autoCancelReadWriteLock.readLock());

        this.writeLock = new ReleasableLock(autoCancelReadWriteLock.writeLock());

        this.log = new Log(this.mainManager);
    }

    public void stop() {
        this.log.stop();
    }

    public void onTaskCreate(Object task) throws AssertionError {        
        TaskWrapper wrappedTask = new TaskWrapper(task);

        CancellableID parentCancellableID = null;

        if (wrappedTask.getParentTaskID() != -1L) {
            assert this.tasks.containsKey(wrappedTask.getParentTaskID()) : "Can't find parent task";
            assert this.cancellableIDTaskBiMap.containsValue(this.tasks.get(wrappedTask.getParentTaskID())) : "Can't find parent task";
            parentCancellableID = this.cancellableIDTaskBiMap.inverse().get(wrappedTask);
        }
        else {
            parentCancellableID = new CancellableID();
        }

        CancellableID cid = this.mainManager.createCancellableIDOnCurrentJavaThreadID(true, task.toString(), parentCancellableID);

        try (ReleasableLock ignored = this.writeLock.acquire()) {
            assert !this.cancellableIDTaskBiMap.containsKey(cid) && !tasks.containsKey(wrappedTask.getTaskID()) : "Do not register one task twice.";

            this.cancellableIDTaskBiMap.put(cid, wrappedTask);
            this.cancellableIDTaskBiMap.forEach((key, value) -> {
                System.out.println(key.toString() + " " + value.toString());
            });
            this.tasks.put(wrappedTask.getTaskID(), wrappedTask);
        }

    }

    public void onTaskExit(Object task) throws AssertionError {
        TaskWrapper wrappedTask = new TaskWrapper(task);
        CancellableID cid = null;

        try (ReleasableLock ignored = this.readLock.acquire()) {
            for (Map.Entry<CancellableID, TaskWrapper> entry : this.cancellableIDTaskBiMap.entrySet()) {
                if (entry.getValue().equals(wrappedTask)) {
                    cid = entry.getKey();
                    break;
                }
            }
        }

        assert cid != null : "Cannot exit an uncreated task.";

        try (ReleasableLock ignored = this.writeLock.acquire()) {
            assert this.cancellableIDTaskBiMap.containsKey(cid) : "Maps should contains the cid to be removed.";
            if (!this.cancellableIDToAsyncRunnables.containsKey(cid)) {
                // task has not been created when runnable starts on the first thread
                // TODO: maybe there is a better way to identify the status
            }
            this.removeCancellableIDFromMaps(cid);
        }

        this.mainManager.destoryCancellableIDOnCurrentJavaThreadID(cid);

        this.log.logCancellableJavaThreadIDInfo(cid, task);
    }

    public void onTaskFinishInThread() throws AssertionError {
        CancellableID cid = this.mainManager.getCancellableIDOnCurrentJavaThreadID();
        if (cid.equals(new CancellableID())) {
            // task has exited
            // TODO: maybe there is a better way to identify the status
            return;
        }

        this.mainManager.unregisterCancellableIDOnCurrentJavaThreadID();
    }

    public void onTaskQueueInThread(Runnable runnable) throws AssertionError {
        assert runnable != null : "Runable cannot be a null pointer.";

        CancellableID cid = this.mainManager.getCancellableIDOnCurrentJavaThreadID();

        // assert !cid.equals(new CancellableID()) : "Task must be running before queuing into threadpool.";
        if (cid.equals(new CancellableID())) {
            // task has not been created yet
            // TODO: maybe there is a better way to identify the status
            return;
        }

        try (ReleasableLock ignored = this.writeLock.acquire()) {
            if (this.cancellableIDToAsyncRunnables.containsKey(cid)) {
                this.cancellableIDToAsyncRunnables.get(cid).add(runnable);
            }
            else {
                this.cancellableIDToAsyncRunnables.put(cid, new ArrayList<Runnable>(Arrays.asList(runnable)));
            }
        }

    }

    public void onTaskStartInThread(Runnable runnable) throws AssertionError {
        assert runnable != null : "Runable cannot be a null pointer.";

        CancellableID cid = null;

        try (ReleasableLock ignored = this.readLock.acquire()) {
            for (Map.Entry<CancellableID, List<Runnable>> entry : this.cancellableIDToAsyncRunnables.entrySet()) {
                if (entry.getValue().contains(runnable)) {
                    cid = entry.getKey();
                    break;
                }
            }
        }

        // assert cid != null : "Cannot start a runnable out of excute entry.";
        if (cid == null) {
            // task has not been created yet
            // TODO: maybe there is a better way to identify the status
            return;
        }

        this.mainManager.registerCancellableIDOnCurrentJavaThreadID(cid);
    }

    private void removeCancellableIDFromMaps(CancellableID cid) {
        this.cancellableIDToAsyncRunnables.remove(cid);
        TaskWrapper wrappedTask = this.cancellableIDTaskBiMap.remove(cid);
        this.tasks.remove(wrappedTask.getTaskID());
    }
}
