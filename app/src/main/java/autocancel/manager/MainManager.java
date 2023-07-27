/*
 * This is the main class of manager layer
 * To Adaptor Layer:
 *     Manage ID
 *     Manage Infrastructure
 * To Core Layer:
 *     Provide data / control api through CID
 */

package autocancel.manager;

import autocancel.app.elasticsearch.AutoCancel;
import autocancel.core.AutoCancelCore;
import autocancel.manager.IDManager;
import autocancel.utils.CancellableID;
import autocancel.utils.JavaThreadID;
import autocancel.utils.ReleasableLock;
import autocancel.utils.CancellableIDGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Queue;

public class MainManager {

    private Queue<String> buffer;

    private IDManager idManager;

    private InfrastructureManager infrastructureManager;

    private CancellableIDGenerator cidGenerator;

    public MainManager() {
        // AutoCancelCore autoCancelCore = new AutoCancelCore(this);
        this.idManager = new IDManager();
        this.cidGenerator = new CancellableIDGenerator();
    }

    public void startNewVersion() {

    }

    public void start() {
        
    }

    public void stop() {

    }

    private CancellableID createCancellable(JavaThreadID jid) {
        CancellableID cid = this.cidGenerator.generate();
        this.idManager.setCancellableIDAndJavaThreadID(cid, jid);
        // TODO: Connect AutoCancelCore
        return cid;
    }

    // public CancellableID getCancellableIDOfJavaThreadID(JavaThreadID jid) {
    //     return this.idManager.getCancellableIDOfJavaThreadID(jid);
    // }

    // public void setCancellableIDAndJavaThreadID(CancellableID cid, JavaThreadID jid) {
    //     this.idManager.setCancellableIDAndJavaThreadID(cid, jid);
    // }

    public void registerCancellableIDOnCurrentJavaThreadID(CancellableID cid) {
        JavaThreadID jid = new JavaThreadID(Thread.currentThread().getId());

        assert !cid.equals(new CancellableID()) : "Cannot register an invalid cancellable id.";

        this.idManager.setCancellableIDAndJavaThreadID(cid, jid);
    }

    public void unregisterCancellableIDOnCurrentJavaThreadID() {
        JavaThreadID jid = new JavaThreadID(Thread.currentThread().getId());
        CancellableID cid = this.idManager.getCancellableIDOfJavaThreadID(jid);

        assert !cid.equals(new CancellableID()) : "Task must be running before finishing.";

        this.idManager.setCancellableIDAndJavaThreadID(new CancellableID(), jid);
    }

    public CancellableID createCancellableIDOnCurrentJavaThreadID() {
        JavaThreadID jid = new JavaThreadID(Thread.currentThread().getId());
        CancellableID cid = this.createCancellable(jid);

        return cid;
    }

    public void destoryCancellableIDOnCurrentJavaThreadID(CancellableID cid) {
        // TODO: Connect AutoCancelCore
    }

    public CancellableID getCancellableIDOnCurrentJavaThreadID() {
        JavaThreadID jid = new JavaThreadID(Thread.currentThread().getId());
        CancellableID cid = this.idManager.getCancellableIDOfJavaThreadID(jid);

        return cid;
    }

}
