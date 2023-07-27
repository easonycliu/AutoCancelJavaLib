package autocancel.core;

import autocancel.manager.MainManager;
import autocancel.utils.Cancellable;
import autocancel.utils.id.CancellableID;
import autocancel.core.monitor.MainMonitor;

import java.util.Map;
import java.util.HashMap;
import java.lang.Thread;

public class AutoCancelCore {

    private MainManager mainManager;

    private MainMonitor mainMonitor;

    private Map<CancellableID, Cancellable> cancellables;

    public AutoCancelCore(MainManager mainManager) {
        this.mainManager = mainManager;
        this.cancellables = new HashMap<CancellableID, Cancellable>();
        this.mainMonitor = new MainMonitor(this.mainManager, this.cancellables);
        
    }

    // public Thread StartCoreOnNewThread() {

    // }
}
