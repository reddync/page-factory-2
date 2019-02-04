package ru.sbtqa.tag.pagefactory.tasks;

import java.util.ArrayList;
import java.util.List;

public class SetupTaskHandler {

    private static ThreadLocal<List<Task>> tasks = ThreadLocal.withInitial(ArrayList::new);

    private SetupTaskHandler() {
    }

    public static void addTask(Task task) {
        if (!tasks.get().stream().anyMatch(task1 -> task1.getClass() == task.getClass())) {
            tasks.get().add(task);
        }
    }

    public static void handleTasks() {
        for (Task task : tasks.get()) {
            task.handle();
        }
        tasks.get().clear();
    }
}