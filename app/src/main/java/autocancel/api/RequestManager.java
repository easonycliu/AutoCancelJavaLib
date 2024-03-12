package autocancel.api;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import autocancel.utils.id.CancellableID;

public class RequestManager {

	private Consumer<Object> requestSender;

	private ConcurrentMap<CancellableID, Object> requestMap;

	public RequestManager() {
		this.requestSender = null;
		this.requestMap = new ConcurrentHashMap<CancellableID, Object>();
	}

	public void onRequestReceive(CancellableID cid, Object request) {
		this.requestMap.put(cid, request);
	}

	public void setRequestSender(Consumer<Object> requestSender) {
		if (this.requestSender == null) {
			this.requestSender = requestSender;
			System.out.println("Request sender set");
		}
	}

	public void reexecuteRequestOfTask(CancellableID cid) {
		if (this.requestSender != null) {
			Object request = requestMap.get(cid);
			if (request != null) {
				this.requestSender.accept(request);
			}
		}
	}

}
