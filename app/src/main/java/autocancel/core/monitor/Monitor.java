package autocancel.core.monitor;

import autocancel.utils.id.CancellableID;

public interface Monitor {

    public void updateResource(CancellableID cid);
}
