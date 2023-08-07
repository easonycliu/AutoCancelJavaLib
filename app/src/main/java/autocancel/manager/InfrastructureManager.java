/*
 * Manage Infrastructure:
 *     Get resource data from specific infrastructure according to settings
 */

package autocancel.manager;

import autocancel.infrastructure.AbstractInfrastructure;
import autocancel.infrastructure.jvm.JavaThreadStatusReader;
import autocancel.infrastructure.linux.LinuxThreadStatusReader;
import autocancel.utils.Resource.ResourceType;
import autocancel.utils.id.CancellableID;
import autocancel.utils.id.JavaThreadID;

import java.util.concurrent.atomic.AtomicInteger;

public class InfrastructureManager {

    private AtomicInteger version;

    private JavaThreadStatusReader javaThreadStatusReader;

    private LinuxThreadStatusReader nativeThreadStatusReader;
    
    public InfrastructureManager() {
        this.version = new AtomicInteger();
        this.javaThreadStatusReader = new JavaThreadStatusReader();
        this.nativeThreadStatusReader = new LinuxThreadStatusReader();
    }

    public Double getSpecifiedTypeResourceLatest(JavaThreadID jid, ResourceType type) {
        // TODO: get resource from infrastructure
        AbstractInfrastructure infrastructure = this.getInfrastructure(type);
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
                infrastructure = this.nativeThreadStatusReader;
                break;
            case MEMORY:
                infrastructure = this.javaThreadStatusReader;
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