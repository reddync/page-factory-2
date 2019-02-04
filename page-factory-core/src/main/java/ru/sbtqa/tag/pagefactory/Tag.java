package ru.sbtqa.tag.pagefactory;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import org.junit.AfterClass;
import ru.sbtqa.tag.pagefactory.environment.Environment;
import ru.sbtqa.tag.pagefactory.tasks.DisposeTaskHandler;
import ru.sbtqa.tag.pagefactory.tasks.SetupTaskHandler;

public class Tag {

//    @Before
//    public void setup() {
//        SetupTaskHandler.handleTasks();
//    }
//
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
