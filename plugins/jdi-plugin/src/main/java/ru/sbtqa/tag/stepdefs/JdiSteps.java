package ru.sbtqa.tag.stepdefs;

public class JdiSteps extends WebGenericSteps{

    public JdiSteps() {
        super();
        JdiSetupSteps.initJDI();
    }
}
