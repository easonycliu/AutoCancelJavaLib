/*
 * Manage Infrastructure:
 *     Get resource data from specific infrastructure according to settings
 */

package autocancel.manager;

import autocancel.infrastructure.AbstractInfrastructure;
import autocancel.infrastructure.jvm.JavaThreadStatusReader;
import autocancel.infrastructure.linux.LinuxThreadStatusReader;
import autocancel.utils.Settings;
import autocancel.utils.Resource.ResourceType;
import autocancel.utils.id.CancellableID;
import autocancel.utils.id.JavaThreadID;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class InfrastructureManager {

    private AtomicInteger version;

    private final Map<String, AbstractInfrastructure> infrastructures;
    
    public InfrastructureManager() {
        this.version = new AtomicInteger();
        this.infrastructures = Map.of(
            "JVM", new JavaThreadStatusReader(),
            "Linux", new LinuxThreadStatusReader()
        );
    }

    public Double getSpecifiedTypeResourceLatest(JavaThreadID jid, ResourceType type) {
        AbstractInfrastructure infrastructure = this.getInfrastructure(type);
        assert infrastructure != null : String.format("Unsupported resource type: %s", type.toString());
        Double resource = infrastructure.getResource(jid, type, this.version.get());
        return resource;
    }

    public void startNewVersion() {
        this.version.incrementAndGet();
    }

    private AbstractInfrastructure getInfrastructure(ResourceType type) {
        // TODO: use infrastructure according to settings
        AbstractInfrastructure infrastructure;
        switch (type) {
            case CPU:
                infrastructure = this.infrastructures.get((String)((Map<?, ?>)Settings.getSetting("monitor_resources")).get("CPU"));
                break;
            case MEMORY:
                infrastructure = this.infrastructures.get((String)((Map<?, ?>)Settings.getSetting("monitor_resources")).get("MEMORY"));
                break;
            case NULL:
                infrastructure = null;
                break;
            default:
                infrastructure = null;
                break;
        }

        return infrastructure;
    }
}