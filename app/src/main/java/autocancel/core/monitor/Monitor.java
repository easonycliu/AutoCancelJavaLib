package autocancel.core.monitor;

import autocancel.utils.CancellableID;

public interface Monitor {

    public void updateResource(CancellableID cid);
}
