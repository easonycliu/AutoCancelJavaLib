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
import autocancel.core.utils.OperationMethod;
import autocancel.core.utils.OperationRequest;
import autocancel.utils.ReleasableLock;
import autocancel.utils.Resource.ResourceType;
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
import java.util.concurrent.atomic.AtomicInteger;

public class MainManager {

    private Queue<OperationRequest> managerRequestToCoreBuffer;

    private IDManager idManager;

    private InfrastructureManager infrastructureManager;

    private CancellableIDGenerator cidGenerator;

    private Thread autoCancelCoreThread;

    public MainManager() {
        this.managerRequestToCoreBuffer = new LinkedList<OperationRequest>();
        this.idManager = new IDManager();
        this.infrastructureManager = new InfrastructureManager();
        this.cidGenerator = new CancellableIDGenerator();
        this.autoCancelCoreThread = null;
    }

    public void start() throws AssertionError {
        assert this.autoCancelCoreThread == null : "AutoCancel core thread has been started";
        AutoCancelCore autoCancelCore = new AutoCancelCore(this);
        this.autoCancelCoreThread = new Thread() {
            @Override
            public void run() {
                autoCancelCore.start();
            }
        };
        this.autoCancelCoreThread.start();
    }

    public void stop() throws AssertionError {
        assert this.autoCancelCoreThread != null : "AutoCancel core thread has to be started";
        this.autoCancelCoreThread.interrupt();
    }

    public void startNewVersion() {
        this.infrastructureManager.startNewVersion();
    }

    private CancellableID createCancellable(JavaThreadID jid, Boolean isCancellable) {
        CancellableID cid = this.cidGenerator.generate();
        this.idManager.setCancellableIDAndJavaThreadID(cid, jid, IDInfo.Status.RUN);

        OperationRequest request = new OperationRequest(OperationMethod.CREATE, cid);
        request.addRequestParam("is_cancellable", isCancellable);
        this.putManagerRequestToCore(request);

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

        this.idManager.setCancellableIDAndJavaThreadID(cid, jid, IDInfo.Status.RUN);
    }

    public void unregisterCancellableIDOnCurrentJavaThreadID() {
        JavaThreadID jid = new JavaThreadID(Thread.currentThread().getId());
        CancellableID cid = this.idManager.getCancellableIDOfJavaThreadID(jid);

        assert !cid.equals(new CancellableID()) : "Task must be running before finishing.";

        this.idManager.setCancellableIDAndJavaThreadID(cid, jid, IDInfo.Status.EXIT);
    }

    public CancellableID createCancellableIDOnCurrentJavaThreadID(Boolean isCancellable) {
        JavaThreadID jid = new JavaThreadID(Thread.currentThread().getId());
        CancellableID cid = this.createCancellable(jid, isCancellable);

        return cid;
    }

    public void destoryCancellableIDOnCurrentJavaThreadID(CancellableID cid) {
        JavaThreadID jid = new JavaThreadID(Thread.currentThread().getId());
        CancellableID cidReadFromManager = this.idManager.getCancellableIDOfJavaThreadID(jid);

        assert cid.equals(cidReadFromManager) : "Input cancellable id is not running on the current java thread id";

        // BUGGY
        this.idManager.setCancellableIDAndJavaThreadID(cidReadFromManager, jid, IDInfo.Status.EXIT);
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

    public void logJavaThreadCancellableIDInfo(JavaThreadID jid) {
        List<IDInfo<CancellableID>> cancellableIDInfos = this.idManager.getAllCancellableIDInfoOfJavaThreadID(jid);

        // TODO: add config to jidinfo save path
        try (FileWriter jidInfoWriter = new FileWriter("/tmp/jidinfo", true)) {
            jidInfoWriter.write(String.format("========== Java Thread %s ==========\n", jid.toString()));
            for (IDInfo<CancellableID> cancellableIDInfo : cancellableIDInfos) {
                jidInfoWriter.write(cancellableIDInfo.toString() + "\n");
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

    public OperationRequest getManagerRequestToCoreWithoutLock() {
        OperationRequest request;
        request = this.managerRequestToCoreBuffer.poll();
        return request;
    }

    public Integer getManagerRequestToCoreBufferSize() {
        Integer size;
        synchronized(this.managerRequestToCoreBuffer) {
            size = this.managerRequestToCoreBuffer.size();
        }
        return size;
    }

    public Double getSpecifiedTypeResource(CancellableID cid, ResourceType type) {
        Double resource = 0.0;
        List<JavaThreadID> javaThreadIDs = this.idManager.getJavaThreadIDOfCancellableID(cid);
        for (JavaThreadID javaThreadID : javaThreadIDs) {
            resource += this.infrastructureManager.getSpecifiedTypeResourceLatest(javaThreadID, type);
        }
        return resource;
    }

}
