package autocancel.app.elasticsearch;

import autocancel.manager.MainManager;
import autocancel.app.elasticsearch.TaskWrapper;
import autocancel.utils.ReleasableLock;
import autocancel.utils.id.CancellableID;
import autocancel.utils.id.JavaThreadID;
import autocancel.utils.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.List;
import java.lang.Thread;

public class AutoCancel {
    
    private static Boolean started = false;

    private static MainManager mainManager = new MainManager();

    private static TaskTracker taskTracker = new TaskTracker(AutoCancel.mainManager);

    private static Resource resourceTracker = new Resource(AutoCancel.mainManager);

    private static Boolean warnNotStarted = true;

    private static Control controller = null;

    public static void start(Consumer<CancellableID> canceller) {
        AutoCancel.mainManager.start(null);
        AutoCancel.controller = new Control(AutoCancel.mainManager, canceller);
        started = true;
        Logger.systemWarn("AutoCancel started.");
    }

    public static void stop() {
        if (AutoCancel.started) {
            AutoCancel.taskTracker.stop();
            AutoCancel.mainManager.stop();
        }
        else if (warnNotStarted) {
            Logger.systemWarn("You should start lib AutoCancel first.");
            AutoCancel.warnNotStarted = false;
        }
    }

    public static void onTaskCreate(Object task) {
        if (AutoCancel.started) {
            AutoCancel.taskTracker.onTaskCreate(task);
        }
        else if (warnNotStarted) {
            Logger.systemWarn("You should start lib AutoCancel first.");
            AutoCancel.warnNotStarted = false;
        }
    }

    public static void onTaskExit(Object task) {
        if (AutoCancel.started) {
            AutoCancel.taskTracker.onTaskExit(task);
        }
        else if (warnNotStarted) {
            Logger.systemWarn("You should start lib AutoCancel first.");
            AutoCancel.warnNotStarted = false;
        }
    }

    public static void onTaskFinishInThread() {
        if (AutoCancel.started) {
            AutoCancel.taskTracker.onTaskFinishInThread();
        }
        else if (warnNotStarted) {
            Logger.systemWarn("You should start lib AutoCancel first.");
            AutoCancel.warnNotStarted = false;
        }
    }

    public static void onTaskQueueInThread(Runnable runnable) {
        if (AutoCancel.started) {
            AutoCancel.taskTracker.onTaskQueueInThread(runnable);
        }
        else if (warnNotStarted) {
            Logger.systemWarn("You should start lib AutoCancel first.");
            AutoCancel.warnNotStarted = false;
        }
    }

    public static void onTaskStartInThread(Runnable runnable) {
        if (AutoCancel.started) {
            AutoCancel.taskTracker.onTaskStartInThread(runnable);
        }
        else if (warnNotStarted) {
            Logger.systemWarn("You should start lib AutoCancel first.");
            AutoCancel.warnNotStarted = false;
        }
    }

    public static void addResourceUsage(String name, Double value) {
        if (AutoCancel.started) {
            AutoCancel.resourceTracker.addResourceUsage(name, value);
        }
        else if (warnNotStarted) {
            Logger.systemWarn("You should start lib AutoCancel first.");
            AutoCancel.warnNotStarted = false;
        }
    }

    public static void startResourceWait(String name) {
        if (AutoCancel.started) {
            AutoCancel.resourceTracker.startResourceEvent(name, "wait");
        }
        else if (warnNotStarted) {
            Logger.systemWarn("You should start lib AutoCancel first.");
            AutoCancel.warnNotStarted = false;
        }
    }

    public static void endResourceWait(String name, Long timestamp) {
        if (AutoCancel.started) {
            AutoCancel.resourceTracker.endResourceEvent(name, "wait", timestamp);
        }
        else if (warnNotStarted) {
            Logger.systemWarn("You should start lib AutoCancel first.");
            AutoCancel.warnNotStarted = false;
        }
    }

    public static Long onLockWait(String name) {
        Long timestamp = -1L;
        if (AutoCancel.started) {
            timestamp = AutoCancel.resourceTracker.onLockWait(name);
        }
        else if (warnNotStarted) {
            Logger.systemWarn("You should start lib AutoCancel first.");
            AutoCancel.warnNotStarted = false;
        }
        return timestamp;
    }

    public static Long onLockGet(String name, Long timestamp) {
        Long nextTimestamp = -1L;
        if (AutoCancel.started) {
            nextTimestamp = AutoCancel.resourceTracker.onLockGet(name, timestamp);
        }
        else if (warnNotStarted) {
            Logger.systemWarn("You should start lib AutoCancel first.");
            AutoCancel.warnNotStarted = false;
        }
        return nextTimestamp;
    }

    public static void onLockRelease(String name, Long timestamp) {
        if (AutoCancel.started) {
            AutoCancel.resourceTracker.onLockRelease(name, timestamp);
        }
        else if (warnNotStarted) {
            Logger.systemWarn("You should start lib AutoCancel first.");
            AutoCancel.warnNotStarted = false;
        }
    }

    public static void cancel(CancellableID cid) {
        if (AutoCancel.started) {
            AutoCancel.controller.cancel(cid);
        }
        else if (warnNotStarted) {
            Logger.systemWarn("You should start lib AutoCancel first.");
            AutoCancel.warnNotStarted = false;
        }
    }

}