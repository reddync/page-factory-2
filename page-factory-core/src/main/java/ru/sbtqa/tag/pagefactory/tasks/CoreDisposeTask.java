package ru.sbtqa.tag.pagefactory.tasks;

import ru.sbtqa.tag.stepdefs.CoreSetupSteps;

public class CoreDisposeTask implements Task {

    @Override
    public void handle() {
        CoreSetupSteps.tearDown();
    }
}
