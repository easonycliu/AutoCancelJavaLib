package autocancel.core.monitor;

import autocancel.core.utils.OperationRequest;
import autocancel.utils.id.CancellableID;

public interface Monitor {

    public OperationRequest updateResource(CancellableID cid);
}
