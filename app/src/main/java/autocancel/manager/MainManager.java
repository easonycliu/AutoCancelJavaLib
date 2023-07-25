/*
 * This is the main class of manager layer
 * To Adaptor Layer:
 *     Manage ID
 *     Manage Infrastructure
 * To Core Layer:
 *     Provide data / control api through CID
 */

package autocancel.manager;

import autocancel.core.AutoCancelCore;
import autocancel.utils.CancellableID;

import java.util.Queue;

public class MainManager {

    private Queue<String> buffer;

    private IDManager idManager;

    private InfrastructureManager infrastructureManager;

    public MainManager() {
        AutoCancelCore autoCancelCore = new AutoCancelCore(this);
    }

    public void startNewVersion() {

    }

    public void start() {
        
    }

    public void stop() {

    }

    // public CancellableID createCancellable() {

    // }
}
