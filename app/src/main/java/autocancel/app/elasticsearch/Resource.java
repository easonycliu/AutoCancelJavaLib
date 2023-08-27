package autocancel.app.elasticsearch;

import autocancel.manager.MainManager;
import autocancel.utils.Settings;
import autocancel.utils.Resource.ResourceType;
import autocancel.utils.logger.Logger;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.List;

public class Resource {

    private final MainManager mainManager;

    private static final Map<String, BiFunction<String, StackTraceElement, Boolean>> lockInfoParser = Map.of(
        "file_name", (name, stackTraceElement) -> stackTraceElement.getFileName().equals(name),
        "line_number", (name, stackTraceElement) -> name.matches("^[0-9]+$") && stackTraceElement.getLineNumber() == Integer.valueOf(name),
        "class_name", (name, stackTraceElement) -> {
            if (name.endsWith("...")) {
                return stackTraceElement.getClassName().contains(name.substring(0, name.length() - 3));
            }
            else {
                return stackTraceElement.getClassName().endsWith(name);
            }
        },
        "method_name", (name, stackTraceElement) -> stackTraceElement.getMethodName().equals(name)
    );

    public Resource(MainManager mainManager) {
        this.mainManager = mainManager;
    }
    
    public void addResourceUsage(String name, Double value) {
        this.mainManager.updateCancellableGroup(name, value);
    }

    private void addResourceEventDuration(String name, String event, Long value) {
        switch (event) {
            case "wait": 
                this.mainManager.updateResource(ResourceType.LOCK, name, Map.of("wait_time", value));
                break;
            case "occupy":
                this.addResourceUsage(name, Double.valueOf(value));
                break;
            default:
                break;
        }
    }

    public Long startResourceEvent(String name, String event) {
        // System.out.println("Start " + event + " for resource " + name);
        return System.nanoTime();
    }

    public void endResourceEvent(String name, String event, Long timestamp) {
        // System.out.println("End " + event + " for resource " + name);
        Long duration = System.nanoTime() - timestamp;
        this.addResourceEventDuration(name, event, duration);
    }

    public Long onLockWait(String name) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        // getStackTrace() <-- Resource.onLockWait() <-- Autocancel.onLockWait() <-- ReleasableLock.acquire() <-- Target <-- ...
        // to reach the target, it must have at least 5 elements
        // position sensitive! Do not use it in the plase other than ReleasableLock.acquire()
        // if want to track the lock waiting time of other types of locks, using startResourceWait(String name)

        Long timestamp = -1L;

        if (stackTraceElements.length >= 5) {
            // System.out.println("Classname: " + stackTraceElements[4].getClassName() + 
            // " Filename: " + stackTraceElements[4].getFileName() + 
            // " Methodname: " + stackTraceElements[4].getMethodName() +
            // " Linenumber: " + stackTraceElements[4].getLineNumber());
            if (this.isMonitorTarget(stackTraceElements[4])) {
                // System.out.println("Find lock at " + stackTraceElements[4].toString());
                timestamp = this.startResourceEvent(name, "wait");
            }
        }

        return timestamp;
    }

    public Long onLockGet(String name, Long timestamp) {
        Long nextTimestamp = -1L;
        if (timestamp > 0) {
            this.endResourceEvent(name, "wait", timestamp);
            nextTimestamp = this.startResourceEvent(name, "occupy");
        }
        return nextTimestamp;
    }

    public void onLockRelease(String name, Long timestamp) {
        if (timestamp > 0) {
            this.endResourceEvent(name, "occupy", timestamp);
        }
    }

    private Boolean isMonitorTarget(StackTraceElement stackTraceElement) {
        List<?> monitorLocks = (List<?>) Settings.getSetting("monitor_locks");
        
        for (Object monitorLock : monitorLocks) {
            Boolean monitorTarget = true;
            for (Map.Entry<?, ?> entries : ((Map<?, ?>) monitorLock).entrySet()) {
                if (Resource.lockInfoParser.containsKey((String) entries.getKey())) {
                    if (!Resource.lockInfoParser.get((String) entries.getKey()).apply((String) entries.getValue(), stackTraceElement)) {
                        monitorTarget = false;
                        break;
                    }
                }
                else {
                    Logger.systemWarn("Do not support " + ((String) entries.getKey()) + ". Here are supported keys: " + Resource.lockInfoParser.keySet().toString());
                    monitorTarget = false;
                    break;
                }
            }
            if (monitorTarget) {
                // find one matches all location requirements
                return true;
            }
        }

        return false;
    }
}
