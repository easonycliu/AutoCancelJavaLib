package autocancel.app.elasticsearch;

import autocancel.manager.MainManager;
import autocancel.utils.resource.ResourceType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;

public class Resource {

    private final static String NULL = "";

    private final MainManager mainManager;

    private ConcurrentMap<String, String> monitoredLock;

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
        this.monitoredLock = new ConcurrentHashMap<String, String>();
    }
    
    public void addResourceUsage(ResourceType type, String name, Map<String, Object> resourceUpdateInfo) {
        this.mainManager.updateCancellableGroup(type, name, resourceUpdateInfo);
    }

    private void addResourceEventDuration(String name, String event, Long value) {
        switch (event) {
            case "wait": 
                this.mainManager.updateCancellableGroup(ResourceType.QUEUE, name, Map.of("wait_time", value));
                break;
            case "occupy":
                this.mainManager.updateCancellableGroup(ResourceType.QUEUE, name, Map.of("occupy_time", value));
                break;
            default:
                break;
        }
    }

    public Long startResourceEvent(String name, String event) {
        System.out.println(String.format("Thread %s start %s on resource %s", Thread.currentThread().getName(), event, name));
        return System.nanoTime();
    }

    public void endResourceEvent(String name, String event, Long timestamp) {
        // System.out.println("End " + event + " for resource " + name);
        Long duration = System.nanoTime() - timestamp;
        System.out.println(String.format("Thread %s spend %d ns %s on resource %s", Thread.currentThread().getName(), duration, event, name));
        this.addResourceEventDuration(name, event, duration);
    }
}
