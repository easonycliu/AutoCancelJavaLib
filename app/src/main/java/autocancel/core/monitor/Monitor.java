package autocancel.core.monitor;

import java.util.List;

import autocancel.core.utils.OperationRequest;
import autocancel.utils.id.CancellableID;

public interface Monitor { public List<OperationRequest> updateResource(CancellableID cid); }
