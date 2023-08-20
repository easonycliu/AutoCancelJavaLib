package autocancel.core;

import autocancel.manager.MainManager;
import autocancel.utils.id.CancellableID;
import autocancel.utils.logger.Logger;
import autocancel.core.monitor.MainMonitor;
import autocancel.core.utils.OperationRequest;
import autocancel.core.utils.ResourceUsage;
import autocancel.core.utils.Cancellable;
import autocancel.core.utils.CancellableGroup;
import autocancel.core.utils.OperationMethod;
import autocancel.utils.Settings;
import autocancel.utils.Resource.ResourceType;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import java.util.HashMap;
import java.lang.Thread;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class AutoCancelCore {

    private MainManager mainManager;

    private MainMonitor mainMonitor;

    private Map<CancellableID, CancellableGroup> rootCancellableToCancellableGroup;

    private Map<CancellableID, Cancellable> cancellables;

    private RequestParser requestParser;

    private Logger logger;

    public AutoCancelCore(MainManager mainManager) {
        this.mainManager = mainManager;
        this.cancellables = new HashMap<CancellableID, Cancellable>();
        this.rootCancellableToCancellableGroup = new HashMap<CancellableID, CancellableGroup>();
        this.mainMonitor = new MainMonitor(this.mainManager, this.cancellables, this.rootCancellableToCancellableGroup);
        this.requestParser = new RequestParser();
        this.logger = new Logger((String) Settings.getSetting("path_to_logs"), "corerequest", 10000);
    }

    public void start() {
        while (!Thread.interrupted()) {
            try {
                this.logger.log(String.format("Current time: %d\n", System.currentTimeMillis()));
                Integer requestBufferSize = this.mainManager.getManagerRequestToCoreBufferSize();
                for (Integer ignore = 0; ignore < requestBufferSize; ++ignore) {
                    OperationRequest request = this.mainManager.getManagerRequestToCore();
                    this.requestParser.parse(request);
                }

                this.mainMonitor.updateTasksResources();

                Integer updateBufferSize = this.mainMonitor.getMonitorUpdateToCoreBufferSizeWithoutLock();
                for (Integer ignore = 0; ignore < updateBufferSize; ++ignore) {
                    OperationRequest request = this.mainMonitor.getMonitorUpdateToCoreWithoutLock();
                    this.requestParser.parse(request);
                }

                Thread.sleep((Long) Settings.getSetting("core_update_cycle_ms")));
            }
            catch (InterruptedException e) {
                break;
            }
        }
        this.stop();
    }

    private void stop() {
        this.logger.close();
        System.out.println("Recieve interrupt, exit");
    }

    private class RequestParser {
        ParamHandlers paramHandlers;

        public RequestParser() {
            this.paramHandlers = new ParamHandlers();
        }

        public void parse(OperationRequest request) {
            logger.log(request.toString() + "\n");
            switch (request.getOperation()) {
                case CREATE:
                    create(request);
                    break;
                case RETRIEVE:
                    retrieve(request);
                    break;
                case UPDATE:
                    update(request);
                    break;
                case DELETE:
                    delete(request);
                    break;
                default:
                    break;
            }
        }

        private void create(OperationRequest request) {
            assert request.getParams().containsKey("parent_cancellable_id") : "Must set parent_cancellable_id when create cancellable.";
            assert request.getTarget() != new CancellableID() : "Create operation must have cancellable id set";
            
            CancellableID parentID = (CancellableID) request.getParams().get("parent_cancellable_id");
            CancellableID rootID = (CancellableID) this.paramHandlers.handle("parent_cancellable_id", request);
            Cancellable cancellable = new Cancellable(request.getTarget(), parentID, rootID);
            
            cancellables.put(request.getTarget(), cancellable);
            if (cancellable.isRoot()) {
                rootCancellableToCancellableGroup.put(rootID, new CancellableGroup(cancellable));
            }
            else {
                rootCancellableToCancellableGroup.get(rootID).putCancellable(cancellable);
            }

            Map<String, Object> params = request.getParams();
            for (String key : params.keySet()) {
                this.paramHandlers.handleIndependentParam(key, request);
            }
        }

        private void retrieve(OperationRequest request) {

        }

        private void update(OperationRequest request) {
            Map<String, Object> params = request.getParams();
            for (String key : params.keySet()) {
                this.paramHandlers.handleIndependentParam(key, request);
            }
        }

        private void delete(OperationRequest request) {
            assert request.getTarget() != new CancellableID() : "Create operation must have cancellable id set";
            cancellables.remove(request.getTarget());

            Map<String, Object> params = request.getParams();
            for (String key : params.keySet()) {
                this.paramHandlers.handleIndependentParam(key, request);
            }
        }

    }

    private class ParamHandlers {

        // These parameters' parsing order doesn't matter
        private final Map<String, Consumer<OperationRequest>> independentParamHandlers = Map.of(
            "is_cancellable", request -> this.isCancellable(request),
            "set_value", request -> this.setValue(request),
            "monitor_resource", request -> this.monitorResource(request),
            "cancellable_name", request -> this.cancellableName(request)
        );
        /*
         * Some parameters' parsing order does matter, currently there are:
         * TODO: Find a way to unify them
         */
        private final Map<String, Function<OperationRequest, Object>> functionHandlers = Map.of(
            "parent_cancellable_id", request -> this.parentCancellableID(request)
        );

        public ParamHandlers() {

        }

        @Nullable
        public Object handle(String type, OperationRequest request) {
            Object ret;
            if (this.functionHandlers.containsKey(type)) {
                ret = this.functionHandlers.get(type).apply(request);
            }
            // TODO: Add other handler types
            else {
                ret = null;
                assert false : "Invalid parameter";
            }
            return ret;
        }

        public void handleIndependentParam(String type, OperationRequest request) {
            if (this.functionHandlers.containsKey(type)) {
                // TODO: handle this situation properly
                return;
            }
            assert this.independentParamHandlers.containsKey(type) : "Invalid parameter " + type;
            this.independentParamHandlers.get(type).accept(request);
        }

        private void isCancellable(OperationRequest request) {
            Cancellable cancellable = cancellables.get(request.getTarget());
            if (cancellable.isRoot()) {
                // This is a root cancellable
                // Parameter is_cancellable is useful only if this cancellable is a root cancellable
                // TODO: Add a warning if this is not a root cancellable
                Boolean isCancellable = (Boolean)request.getParams().get("is_cancellable");
                rootCancellableToCancellableGroup.get(cancellable.getID()).setIsCancellable(isCancellable);
            }
        }

        // TODO: Consider changing a function name
        private void setValue(OperationRequest request) {
            Cancellable cancellable = cancellables.get(request.getTarget());
            Double value = (Double)request.getParams().get("set_value");
            rootCancellableToCancellableGroup.get(cancellable.getRootID()).setResourceUsage(request.getResourceType(), value);
        }

        private void monitorResource(OperationRequest request) {
            Cancellable cancellable = cancellables.get(request.getTarget());
            if (cancellable.isRoot()) {
                // This is a root cancellable
                // Parameter monitor_resource is useful only if this cancellable is a root cancellable
                // TODO: Add a warning if this is not a root cancellable
                List<?> resourceTypes = (List<?>)request.getParams().get("monitor_resource");
                CancellableGroup cancellableGroup = rootCancellableToCancellableGroup.get(cancellable.getID());
                for (Object resourceType : resourceTypes) {
                    cancellableGroup.setResourceUsage((ResourceType)resourceType, 0.0);
                }
            }
        }

        private void cancellableName(OperationRequest request) {
            Cancellable cancellable = cancellables.get(request.getTarget());
            String name = (String)request.getParams().get("cancellable_name");
            cancellable.setName(name);
        }

        private CancellableID parentCancellableID(OperationRequest request) {
            CancellableID parentID = (CancellableID) request.getParams().get("parent_cancellable_id");
            CancellableID rootID = null;
            if (parentID.equals(new CancellableID())) {
                // Itself is a root cancellable
                rootID = request.getTarget();
            }
            else{
                rootID = cancellables.get(parentID).getID();
            }
            return rootID;
        }
    }
}
