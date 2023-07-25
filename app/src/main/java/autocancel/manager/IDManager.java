/*
 * Manage ID:
 *     Manage a map between javaTID and CID
 *     This is a dynamic map because a CID can be mapped to different javaTID
 */

package autocancel.manager;

import autocancel.utils.CancellableID;
import autocancel.utils.JavaThreadID;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class IDManager {

    private Map<CancellableID, List<JavaThreadID>> cancellableIDToJavaThreadID;

    private Map<JavaThreadID, List<CancellableID>> javaThreadIDToCancellableID;

    private ReadWriteLock idManagerLock;

    private Lock readLock;

    private Lock writeLock;

    public IDManager() {
        this.idManagerLock = new ReentrantReadWriteLock();
        this.readLock = idManagerLock.readLock();
        this.writeLock = idManagerLock.writeLock();

        this.cancellableIDToJavaThreadID = new HashMap<CancellableID, List<JavaThreadID>>();
        this.javaThreadIDToCancellableID = new HashMap<JavaThreadID, List<CancellableID>>();

    }

    public JavaThreadID getJavaThreadIDOfCancellableID(CancellableID cid) {
        JavaThreadID javaThreadID;

        try {
            this.readLock.lock();
            if (this.cancellableIDToJavaThreadID.containsKey(cid)) {
                List<JavaThreadID> javaThreadIDList = this.cancellableIDToJavaThreadID.get(cid);
                javaThreadID = javaThreadIDList.get(javaThreadIDList.size() - 1);
            }
            else {
                javaThreadID = new JavaThreadID();
            }
        }
        finally {
            this.readLock.unlock();
        }

        return javaThreadID;
    }

    public CancellableID getCancellableIDOfJavaThreadID(JavaThreadID jid) {
        CancellableID cancellableID;

        try {
            this.readLock.lock();
            if (this.javaThreadIDToCancellableID.containsKey(jid)) {
                List<CancellableID> cancellableIDList = this.javaThreadIDToCancellableID.get(jid);
                cancellableID = cancellableIDList.get(cancellableIDList.size() - 1);
            }
            else {
                cancellableID = new CancellableID();
            }
        }
        finally {
            this.readLock.unlock();
        }
        
        return cancellableID;
    }

    public void setCancellableIDAndJavaThreadID(CancellableID cid, JavaThreadID jid) {
        try {
            this.writeLock.lock();
            this.doSetCancellableIDAndJavaThreadID(cid, jid);
        }
        finally {
            this.writeLock.unlock();
        }
    }

    private void doSetCancellableIDAndJavaThreadID(CancellableID cid, JavaThreadID jid) {
        if (this.cancellableIDToJavaThreadID.containsKey(cid)) {
            this.cancellableIDToJavaThreadID.get(cid).add(jid);
        }
        else {
            this.cancellableIDToJavaThreadID.put(cid, new ArrayList<JavaThreadID>(Arrays.asList(jid)));
        }

        if (this.javaThreadIDToCancellableID.containsKey(jid)) {
            this.javaThreadIDToCancellableID.get(jid).add(cid);
        }
        else {
            this.javaThreadIDToCancellableID.put(jid, new ArrayList<CancellableID>(Arrays.asList(cid)));
        }

    }
}