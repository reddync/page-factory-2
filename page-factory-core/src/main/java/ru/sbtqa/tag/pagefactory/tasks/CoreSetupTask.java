package ru.sbtqa.tag.pagefactory.tasks;

import ru.sbtqa.tag.stepdefs.CoreSetupSteps;

public class CoreSetupTask implements SetupTask {
    @Override
    public void handle() {
        CoreSetupSteps.setUp();
    }
}
