package autocancel.api;

import autocancel.utils.id.CancellableID;

import java.util.function.Consumer;

public class Control {

    private final Consumer<Object> canceller;

    public Control(Consumer<Object> canceller) {
        this.canceller = canceller;
    }

    public void cancel(CancellableID cid) {
        if (cid.isValid()) {
            this.canceller.accept(cid.toLong());
        }
    }
}
