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

public class MainManager {

    public MainManager() {
        AutoCancelCore autoCancelCore = new AutoCancelCore(this);
    }

    public void startNewVersion() {

    }
}
