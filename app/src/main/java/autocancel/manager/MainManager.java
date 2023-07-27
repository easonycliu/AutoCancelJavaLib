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
import autocancel.core.utils.OperationRequest;
import autocancel.utils.ReleasableLock;
import autocancel.utils.id.CancellableID;
import autocancel.utils.id.CancellableIDGenerator;
import autocancel.utils.id.JavaThreadID;
import autocancel.utils.id.IDInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

public class MainManager {

    private Queue<OperationRequest> managerRequestToCoreBuffer;

    private IDManager idManager;

    private InfrastructureManager infrastructureManager;

    private CancellableIDGenerator cidGenerator;

    public MainManager() {
        // AutoCancelCore autoCancelCore = new AutoCancelCore(this);
        this.managerRequestToCoreBuffer = new LinkedList<OperationRequest>();
        this.idManager = new IDManager();
        this.infrastructureManager = new InfrastructureManager();
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

    public void logCancellableJavaThreadIDInfo(CancellableID cid) {
        List<IDInfo<JavaThreadID>> javaThreadIDInfos = this.idManager.getAllJavaThreadIDInfoOfCancellableID(cid);

        // TODO: add config to jidinfo save path
        try (FileWriter jidInfoWriter = new FileWriter("/tmp/jidinfo", true)) {
            jidInfoWriter.write(String.format("========== Cancellable %s ==========\n", cid.toString()));
            for (IDInfo<JavaThreadID> javaThreadIDInfo : javaThreadIDInfos) {
                jidInfoWriter.write(javaThreadIDInfo.toString() + "\n");
            }
        }
        catch (IOException e) {

        }
    }

    public void putManagerRequestToCore(OperationRequest request) {
        synchronized(this.managerRequestToCoreBuffer) {
            this.managerRequestToCoreBuffer.add(request);
        }
    }

    public OperationRequest getManagerRequestToCore() {
        OperationRequest request;
        synchronized(this.managerRequestToCoreBuffer) {
            request = this.managerRequestToCoreBuffer.poll();
        }
        return request;
    }

    public Integer getManagerRequestToCoreBufferSize() {
        Integer size;
        synchronized(this.managerRequestToCoreBuffer) {
            size = this.managerRequestToCoreBuffer.size();
        }
        return size;
    }

}
