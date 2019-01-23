package ru.sbtqa.tag.stepdefs.ru;

import ru.sbtqa.tag.stepdefs.JdiSetupSteps;
import ru.sbtqa.tag.stepdefs.JdiSteps;

public class JdiStepDefs extends JdiSteps {

    public JdiStepDefs() {
        super();
        JdiSetupSteps.initJDI();
    }

}