package autocancel.app.elasticsearch;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import autocancel.utils.id.CancellableID;

public class TaskWrapper {

    private static Pattern parentPattern = Pattern.compile("(.*)(parentTask=)([^\\s]+)(,)(.*)");

    private static Pattern taskPattern = Pattern.compile("(.*)(Task\\{id=)([0-9]+)(,)(.*)");
    
    private Object task;

    private Long taskID;

    private Long parentID;

    public TaskWrapper(Object task) throws AssertionError {
        assert task.toString().contains("Task") : "Input is not a class Task.";

        this.task = task;

        Matcher parentMatcher = parentPattern.matcher(this.task.toString());

        if (parentMatcher.find()) {
            String id = parentMatcher.group(3);
            if (id.equals("unset")) {
                this.parentID = -1L;
            }
            else {
                String[] items = id.split(":");
                assert items.length == 2 && items[1].matches("^[0-9]+$") : "Illegal task name format " + this.task.toString();
                this.parentID = Long.valueOf(items[1]);
            }
        }
        else {
            assert false : "Illegal task name format " + this.task.toString();
        }

        Matcher taskMatcher = taskPattern.matcher(this.task.toString());

        if (taskMatcher.find()) {
            this.taskID = Long.valueOf(taskMatcher.group(3));
        }
        else {
            assert false : "Illegal task name format " + this.task.toString();
        }

    }

    public Long getParentTaskID() {
        return this.parentID;
    }

    public Long getTaskID() {
        return this.taskID;
    }

    @Override
    public String toString() {
        return "Wrapped" + this.task.toString();
    }

    @Override
    public int hashCode() {
        return this.taskID.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this.hashCode() == o.hashCode();
    }
}
