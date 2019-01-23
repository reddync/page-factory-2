package ru.sbtqa.tag.pagefactory;

import cucumber.api.java.After;
import org.junit.AfterClass;
import ru.sbtqa.tag.pagefactory.environment.Environment;
import ru.sbtqa.tag.pagefactory.tasks.DisposeTaskHandler;

public class Tag {

    @After
    public void dispose() {
        DisposeTaskHandler.handleTasks();
    }

    @AfterClass
    public static void tearDown() {
        if (!Environment.isDriverEmpty()) {
            Environment.getDriverService().demountDriver();
        }
    }
}
