package autocancel.app.elasticsearch;

import autocancel.manager.MainManager;
import autocancel.app.elasticsearch.TaskWrapper;

import java.util.HashMap;
import java.util.Map;
import java.lang.Thread;

public class AutoCancel {
    
    private static Boolean started = false;

    private static MainManager mainManager = new MainManager();

    public static void start() {
        mainManager.start();
        started = true;
    }

    public static void stop() throws AssertionError {
        assert started : "You should start lib AutoCancel first.";
        mainManager.stop();
    }

    public static void onTaskCreated(Object task) throws AssertionError {
        assert started : "You should start lib AutoCancel first.";
        
        TaskWrapper wrappedTask = new TaskWrapper(task);


        Thread.currentThread().getId();
    }
}