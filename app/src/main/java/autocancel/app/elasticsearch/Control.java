package autocancel.app.elasticsearch;

import autocancel.manager.MainManager;
import autocancel.utils.id.CancellableID;

import java.util.function.BiConsumer;

public class Control {

    private final MainManager mainManager;

    private final BiConsumer<Long, String> canceller;

    public Control(MainManager mainManager, BiConsumer<Long, String> canceller) {
        this.mainManager = mainManager;
        this.canceller = canceller;
    }

    public void cancel(CancellableID cid) {
        if (cid.isValid()) {
            this.canceller.accept(cid.toLong(), "Auto Cancel Library");
        }
    }
}
