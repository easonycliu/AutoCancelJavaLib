package autocancel.core.monitor;

import autocancel.core.utils.CancellableID;

public interface Monitor {

    public void updateResource(CancellableID cid);
}
