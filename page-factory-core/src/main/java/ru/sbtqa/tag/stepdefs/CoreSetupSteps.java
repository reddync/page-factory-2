package ru.sbtqa.tag.stepdefs;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import io.qameta.allure.Step;
import ru.sbtqa.tag.pagefactory.environment.Environment;
import ru.sbtqa.tag.pagefactory.find.FindUtils;
import ru.sbtqa.tag.pagefactory.properties.Configuration;
import ru.sbtqa.tag.pagefactory.reflection.DefaultReflection;
import ru.sbtqa.tag.pagefactory.tasks.ConnectToLogTask;
import ru.sbtqa.tag.pagefactory.tasks.CoreDisposeTask;
import ru.sbtqa.tag.pagefactory.tasks.DisposeTaskHandler;
import ru.sbtqa.tag.pagefactory.tasks.KillProcessesTask;
import ru.sbtqa.tag.pagefactory.tasks.SetupTaskHandler;
import ru.sbtqa.tag.pagefactory.tasks.StartVideoTask;
import ru.sbtqa.tag.pagefactory.tasks.StopVideoTask;
import ru.sbtqa.tag.pagefactory.tasks.TaskHandler;

public class CoreSetupSteps {

    private static final Configuration PROPERTIES = Configuration.create();


    @Step("Inititalize core infrastructure")
    public static void setUp() {
        TaskHandler.addTask(new ConnectToLogTask());
        TaskHandler.addTask(new KillProcessesTask());
        TaskHandler.addTask(new StartVideoTask());

        Environment.setReflection(new DefaultReflection());
        Environment.setFindUtils(new FindUtils());

        TaskHandler.handleTasks();
        DisposeTaskHandler.addTask(new CoreDisposeTask());
    }


    @Step("Dispose core infrastructure")
    public static void tearDown() {
        TaskHandler.addTask(new StopVideoTask());
        TaskHandler.handleTasks();

        if (!Environment.isDriverEmpty() && !PROPERTIES.getShared()) {
            Environment.getDriverService().demountDriver();
        }
    }

}
