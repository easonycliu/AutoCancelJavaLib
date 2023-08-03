package autocancel.core.utils;

import autocancel.core.utils.OperationMethod;
import autocancel.utils.id.CancellableID;
import autocancel.utils.Resource.ResourceType;

import java.util.Map;
import java.util.HashMap;

public class OperationRequest {
    OperationMethod operation;

    CancellableID target;

    ResourceType resourceType;

    Map<String, Object> params;

    public OperationRequest(OperationMethod operation, CancellableID target) {
        this.operation = operation;
        this.target = target;
        this.resourceType = ResourceType.NULL;
        this.params = new HashMap<String, Object>();
    }

    public OperationRequest(OperationMethod operation, CancellableID target, ResourceType resourceType) {
        this.operation = operation;
        this.target = target;
        this.resourceType = resourceType;
        this.params = new HashMap<String, Object>();
    }

    public OperationRequest(OperationMethod operation, CancellableID target, ResourceType resourceType, Map<String, Object> params) {
        this.operation = operation;
        this.target = target;
        this.resourceType = resourceType;
        this.params = params;
    }

    public void addRequestParam(String key, Object value) {
        this.params.put(key, value);
    }

    public void addRequestParam(Map<String, Object> params) {
        this.params.putAll(params);
    }

    public OperationMethod getOperation() {
        return this.operation;
    }

    public CancellableID getTarget() {
        return this.target;
    }

    public ResourceType getResourceType() {
        return this.resourceType;
    }

    public Map<String, Object> getParams() {
        return this.params;
    }
}
