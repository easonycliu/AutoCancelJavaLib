/*
 * Manage ID:
 *     Manage a map between javaTID and CID
 *     This is a dynamic map because a CID can be mapped to different javaTID
 */

package autocancel.manager;

import autocancel.utils.ReleasableLock;
import autocancel.utils.id.CancellableID;
import autocancel.utils.id.JavaThreadID;
import autocancel.utils.id.IDInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class IDManager {

    private Map<CancellableID, List<IDInfo<JavaThreadID>>> cancellableIDToJavaThreadID;

    private Map<JavaThreadID, List<IDInfo<CancellableID>>> javaThreadIDToCancellableID;

    private ReadWriteLock idManagerLock;

    private ReleasableLock readLock;

    private ReleasableLock writeLock;

    public IDManager() {
        this.idManagerLock = new ReentrantReadWriteLock();
        this.readLock = new ReleasableLock(idManagerLock.readLock());
        this.writeLock = new ReleasableLock(idManagerLock.writeLock());

        this.cancellableIDToJavaThreadID = new HashMap<CancellableID, List<IDInfo<JavaThreadID>>>();
        this.javaThreadIDToCancellableID = new HashMap<JavaThreadID, List<IDInfo<CancellableID>>>();

    }

    public JavaThreadID getJavaThreadIDOfCancellableID(CancellableID cid) {
        JavaThreadID javaThreadID;

        try (ReleasableLock ignored = this.readLock.acquire()) {
            if (this.cancellableIDToJavaThreadID.containsKey(cid)) {
                List<IDInfo<JavaThreadID>> javaThreadIDList = this.cancellableIDToJavaThreadID.get(cid);
                javaThreadID = javaThreadIDList.get(javaThreadIDList.size() - 1).getID();
            }
            else {
                javaThreadID = new JavaThreadID();
            }
        }

        return javaThreadID;
    }

    public CancellableID getCancellableIDOfJavaThreadID(JavaThreadID jid) {
        CancellableID cancellableID;

        try (ReleasableLock ignored = this.readLock.acquire()) {
            if (this.javaThreadIDToCancellableID.containsKey(jid)) {
                List<IDInfo<CancellableID>> cancellableIDList = this.javaThreadIDToCancellableID.get(jid);
                cancellableID = cancellableIDList.get(cancellableIDList.size() - 1).getID();
            }
            else {
                cancellableID = new CancellableID();
            }
        }
        
        return cancellableID;
    }

    public List<IDInfo<JavaThreadID>> getAllJavaThreadIDInfoOfCancellableID(CancellableID cid) {
        List<IDInfo<JavaThreadID>> javaThreadIDInfos;

        try (ReleasableLock ignored = this.readLock.acquire()) {
            if (this.cancellableIDToJavaThreadID.containsKey(cid)) {
                javaThreadIDInfos = this.cancellableIDToJavaThreadID.get(cid);
            }
            else {
                javaThreadIDInfos = new ArrayList<IDInfo<JavaThreadID>>(Arrays.asList(new IDInfo<JavaThreadID>(new JavaThreadID())));
            }
        }

        return javaThreadIDInfos;
    }

    public void setCancellableIDAndJavaThreadID(CancellableID cid, JavaThreadID jid) {
        try (ReleasableLock ignored = this.writeLock.acquire()) {
            this.doSetCancellableIDAndJavaThreadID(cid, jid);
        }
    }

    private void doSetCancellableIDAndJavaThreadID(CancellableID cid, JavaThreadID jid) {
        if (this.cancellableIDToJavaThreadID.containsKey(cid)) {
            this.cancellableIDToJavaThreadID.get(cid).add(new IDInfo<JavaThreadID>(jid));
        }
        else {
            this.cancellableIDToJavaThreadID.put(cid, new ArrayList<IDInfo<JavaThreadID>>(Arrays.asList(new IDInfo<JavaThreadID>(jid))));
        }

        if (this.javaThreadIDToCancellableID.containsKey(jid)) {
            this.javaThreadIDToCancellableID.get(jid).add(new IDInfo<CancellableID>(cid));
        }
        else {
            this.javaThreadIDToCancellableID.put(jid, new ArrayList<IDInfo<CancellableID>>(Arrays.asList(new IDInfo<CancellableID>(cid))));
        }

    }
}
