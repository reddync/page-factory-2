package ru.sbtqa.tag.stepdefs;

import io.qameta.allure.Step;
import static java.lang.ThreadLocal.withInitial;
import ru.sbtqa.tag.pagefactory.PageManager;
import ru.sbtqa.tag.pagefactory.environment.Environment;
import ru.sbtqa.tag.pagefactory.web.tasks.DisposeTask;
import ru.sbtqa.tag.pagefactory.tasks.DisposeTaskHandler;
import ru.sbtqa.tag.pagefactory.tasks.TaskHandler;
import ru.sbtqa.tag.pagefactory.web.drivers.WebDriverService;
import ru.sbtqa.tag.pagefactory.web.tasks.KillAlertTask;

public class WebSetupSteps {

    static final ThreadLocal<WebDriverService> storage = withInitial(WebDriverService::new);

    private WebSetupSteps() {
    }

    public static synchronized void initWeb() {
        PageManager.cachePages();

        if (Environment.isDriverEmpty()) {
            Environment.setDriverService(storage.get());
        }
        DisposeTaskHandler.addTask(new DisposeTask());
    }

    @Step("Dispose Web Plugin")
    public static synchronized void disposeWeb() {
        TaskHandler.addTask(new KillAlertTask());
    }
}
