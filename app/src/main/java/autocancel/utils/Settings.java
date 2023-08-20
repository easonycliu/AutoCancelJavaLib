package autocancel.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

public class Settings {
    
    private final static Map<String, Object> settings = Map.of(
        "path_to_logs", "/usr/share/elasticsearch",
        "core_update_cycle_ms", 100,
        "monitor_resources", Map.of(
            "CPU", "JVM",
            "MEMORY", "Linux"
        )
    );

    public static Object getSetting(String name) {
        assert Settings.settings.containsKey(name) : "invalid setting name: " + name;
        return Settings.settings.get(name);
    }
}
