package autocancel.core.utils;

import autocancel.core.utils.OperationMethod;
import autocancel.utils.id.CancellableID;
import autocancel.utils.Resource.ResourceName;

import java.util.Map;
import java.util.LinkedHashMap;

// TODO: Find a better representaion
public class OperationRequest {

    OperationMethod operation;

    Map<String, Object> params;

    Long nanoTime;

    public OperationRequest(OperationMethod operation, Map<String, Object> basicInfo) {
        this.operation = operation;
        // The iterate order of LinkedHashMap is same as input order
        this.params = new LinkedHashMap<String, Object>();
        this.nanoTime = System.nanoTime();
        this.params.put("basic_info", basicInfo);
    }

    public void addRequestParam(String key, Object value) {
        this.params.put(key, value);
    }

    public OperationMethod getOperation() {
        return this.operation;
    }

    public CancellableID getCancellableID() {
        CancellableID cid;
        if (((Map<?, ?>) this.params.get("basic_info")).containsKey("cancellable_id")) {
            cid = (CancellableID) ((Map<?, ?>) this.params.get("basic_info")).get("cancellable_id");
        }
        else {
            cid = new CancellableID();
        }
        return cid;
    }

    public ResourceName getResourceName() {
        ResourceName resourceName;
        if (((Map<?, ?>) this.params.get("basic_info")).containsKey("resource_name")) {
            resourceName = (ResourceName) ((Map<?, ?>) this.params.get("basic_info")).get("resource_name");
        }
        else {
            resourceName = ResourceName.NULL;
        }
        return resourceName;
    }

    public Map<String, Object> getParams() {
        return this.params;
    }

    @Override
    public String toString() {
        String strRequest = String.format("Time: %d, %s %s %s. ", this.nanoTime, this.operation.toString());
        for (Map.Entry<String, Object> entry : this.params.entrySet()) {
            strRequest = strRequest + String.format("%s: %s; ", entry.getKey(), entry.getValue().toString());
        }
        return strRequest;
    }
}
