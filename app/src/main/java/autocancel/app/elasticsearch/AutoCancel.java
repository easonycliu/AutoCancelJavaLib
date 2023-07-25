package autocancel.app.elasticsearch;

import autocancel.manager.MainManager;
import autocancel.app.elasticsearch.TaskWrapper;
import autocancel.utils.CancellableID;
import autocancel.utils.JavaThreadID;
import autocancel.utils.ReleasableLock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.List;
import java.lang.Thread;

public class AutoCancel {
    
    private static Boolean started = false;

    private static MainManager mainManager = new MainManager();

    private static Map<CancellableID, List<Runnable>> cancellableIDToAsyncRunnables = new HashMap<CancellableID, List<Runnable>>();

    private static Map<CancellableID, TaskWrapper> cancellableIDToTask = new HashMap<CancellableID, TaskWrapper>();

    private static ReadWriteLock autoCancelReadWriteLock = new ReentrantReadWriteLock();

    private static ReleasableLock readLock = new ReleasableLock(autoCancelReadWriteLock.readLock());

    private static ReleasableLock writeLock = new ReleasableLock(autoCancelReadWriteLock.writeLock());

    public static void start() {
        mainManager.start();
        started = true;
    }

    public static void stop() throws AssertionError {
        assert started : "You should start lib AutoCancel first.";
        mainManager.stop();
    }

    public static void onTaskCreate(Object task) throws AssertionError {
        assert started : "You should start lib AutoCancel first.";
        
        TaskWrapper wrappedTask = new TaskWrapper(task);
        JavaThreadID jid = new JavaThreadID(Thread.currentThread().getId());
        CancellableID cid = AutoCancel.mainManager.createCancellable(jid);

        try (ReleasableLock ignored = AutoCancel.writeLock.acquire()) {
            assert !cancellableIDToTask.containsKey(cid) : "Do not register one task twice.";
            cancellableIDToTask.put(cid, wrappedTask);
        }

    }

    public static void onTaskFinish() throws AssertionError {
        assert started : "You should start lib AutoCancel first.";

    }

    public static void onTaskRunAsync() throws AssertionError {
        assert started : "You should start lib AutoCancel first.";

        JavaThreadID jid = new JavaThreadID(Thread.currentThread().getId());



    }
}