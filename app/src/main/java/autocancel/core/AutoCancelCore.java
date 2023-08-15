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
import autocancel.utils.Resource.ResourceType;

import java.util.Map;
import java.util.function.Consumer;
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
        this.mainMonitor = new MainMonitor(this.mainManager, this.cancellables, this.rootCancellableToCancellableGroup);
        this.requestParser = new RequestParser();
        this.logger = new Logger("/tmp/logs", "corerequest", 10000);
    }

    public void start() {
        while (!Thread.interrupted()) {
            // TODO: Maybe this can be added to settings
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

                Thread.sleep(10);
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
        Map<String, Consumer<OperationRequest>> paramHandlers;

        public RequestParser() {
            this.paramHandlers = new HashMap<String, Consumer<OperationRequest>>();

            // These parameters' parsing order doesn't matter
            this.paramHandlers.put("is_cancellable", request -> this.isCancellable(request));
            this.paramHandlers.put("set_value", request -> this.setValue(request));
            this.paramHandlers.put("monitor_resource", request -> this.monitorResource(request));
            this.paramHandlers.put("cancellable_name", request -> this.cancellableName(request));
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
            Cancellable cancellable = new Cancellable();
            assert request.getTarget() != new CancellableID() : "Create operation must have cancellable id set";
            cancellables.put(request.getTarget(), cancellable);

            Map<String, Object> params = request.getParams();
            for (String key : params.keySet()) {
                assert this.paramHandlers.containsKey(key) : "Invalid parameter handler";
                this.paramHandlers.get(key).accept(request);
            }
        }

        private void retrieve(OperationRequest request) {

        }

        private void update(OperationRequest request) {
            Map<String, Object> params = request.getParams();
            for (String key : params.keySet()) {
                assert this.paramHandlers.containsKey(key) : "Invalid parameter handler";
                this.paramHandlers.get(key).accept(request);
            }
        }

        private void delete(OperationRequest request) {
            assert request.getTarget() != new CancellableID() : "Create operation must have cancellable id set";
            cancellables.remove(request.getTarget());

            Map<String, Object> params = request.getParams();
            for (String key : params.keySet()) {
                assert this.paramHandlers.containsKey(key) : "Invalid parameter handler";
                this.paramHandlers.get(key).accept(request);
            }
        }

        private void isCancellable(OperationRequest request) {
            Cancellable cancellable = cancellables.get(request.getTarget());
            if (cancellable.getParentID().equals(new CancellableID())) {
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
            if (cancellable.getParentID().equals(new CancellableID())) {
                // This is a root cancellable
                // Parameter monitor_resource is useful only if this cancellable is a root cancellable
                // TODO: Add a warning if this is not a root cancellable
                List<?> resourceTypes = (List<?>)request.getParams().get("monitor_resource");
                for (Object resourceType : resourceTypes) {
                    cancellable.setResourceUsage((ResourceType)resourceType, 0.0);
                }
            }
        }

        private void cancellableName(OperationRequest request) {
            Cancellable cancellable = cancellables.get(request.getTarget());
            String name = (String)request.getParams().get("cancellable_name");
            cancellable.setName(name);
        }
    }
}
