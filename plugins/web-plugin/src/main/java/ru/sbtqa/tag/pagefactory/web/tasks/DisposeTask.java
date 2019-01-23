package ru.sbtqa.tag.pagefactory.web.tasks;

import io.qameta.allure.Step;
import ru.sbtqa.tag.pagefactory.tasks.Task;
import ru.sbtqa.tag.stepdefs.CoreSetupSteps;
import ru.sbtqa.tag.stepdefs.WebSetupSteps;

public class DisposeTask implements Task {
    @Override
    public void handle() {
        WebSetupSteps.disposeWeb();
    }
}
