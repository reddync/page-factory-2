package ru.sbtqa.tag.pagefactory.tasks;

import io.qameta.allure.Step;
import ru.sbtqa.tag.stepdefs.CoreSetupSteps;

public class DisposeTask implements Task {

    @Override
    @Step("Dispose core")
    public void handle() {
        CoreSetupSteps.tearDown();
    }
}
