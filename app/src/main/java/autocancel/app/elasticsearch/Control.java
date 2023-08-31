package autocancel.app.elasticsearch;

import autocancel.manager.MainManager;
import autocancel.utils.id.CancellableID;

import java.util.function.Consumer;

public class Control {

    private final MainManager mainManager;

    private final Consumer<CancellableID> canceller;

    public Control(MainManager mainManager, Consumer<CancellableID> canceller) {
        this.mainManager = mainManager;
        this.canceller = canceller;
    }

    public void cancel(CancellableID cid) {
        this.canceller.accept(cid);
    }
}
