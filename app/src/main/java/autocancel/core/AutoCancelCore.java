package autocancel.core;

import autocancel.manager.MainManager;
import autocancel.core.monitor.MainMonitor;
import autocancel.core.utils.Cancellable;
import autocancel.core.utils.CancellableID;

import java.util.Map;
import java.util.HashMap;

public class AutoCancelCore {

    private MainManager mainManager;

    private MainMonitor mainMonitor;

    private Map<CancellableID, Cancellable> cancellables;

    public AutoCancelCore(MainManager mainManager) {
        this.mainManager = mainManager;
        this.cancellables = new HashMap<CancellableID, Cancellable>();
        this.mainMonitor = new MainMonitor(this.mainManager, this.cancellables);
        
    }
}
