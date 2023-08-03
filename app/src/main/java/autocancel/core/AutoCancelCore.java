package autocancel.core;

import autocancel.manager.MainManager;
import autocancel.utils.Cancellable;
import autocancel.utils.id.CancellableID;
import autocancel.core.monitor.MainMonitor;
import autocancel.core.utils.OperationRequest;
import autocancel.core.utils.OperationMethod;

import java.util.Map;
import java.util.function.Consumer;
import java.util.HashMap;
import java.lang.Thread;

public class AutoCancelCore {

    private MainManager mainManager;

    private MainMonitor mainMonitor;

    private Map<CancellableID, Cancellable> cancellables;

    private RequestParser requestParser;

    public AutoCancelCore(MainManager mainManager) {
        this.mainManager = mainManager;
        this.cancellables = new HashMap<CancellableID, Cancellable>();
        this.mainMonitor = new MainMonitor(this.mainManager, this.cancellables);
        
    }

    public void start() {
        while (!Thread.interrupted()) {
            // TODO: Maybe this can be added to settings
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                
            }
        }
    }

    private class RequestParser {
        Map<String, Consumer<OperationRequest>> paramHandlers;

        public RequestParser() {

        }

        public void parse(OperationRequest request) {
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

        }

        private void retrieve(OperationRequest request) {

        }

        private void update(OperationRequest request) {

        }

        private void delete(OperationRequest request) {

        }
    }
}
