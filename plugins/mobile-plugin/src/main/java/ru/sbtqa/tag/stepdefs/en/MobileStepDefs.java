package ru.sbtqa.tag.stepdefs.en;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import ru.sbtqa.tag.pagefactory.exceptions.SwipeException;
import ru.sbtqa.tag.pagefactory.mobile.junit.MobileSetupSteps;
import ru.sbtqa.tag.pagefactory.mobile.junit.MobileSteps;

public class MobileStepDefs {

    private MobileSteps mobileSteps = MobileSteps.getInstance();

    @Before(order = 1)
    public void initMobile() {
        MobileSetupSteps.initMobile();
    }

    @And("^user swipes in direction \"(.*?)\" to the text \"(.*?)\"$")
    public void swipeToTextByDirection(String direction, String text) throws SwipeException {
        mobileSteps.swipeToTextByDirection(direction, text);
    }

    @And("^user swipes with match strategy \"(.*?)\" to the text \"(.*?)\"$")
    public void swipeToTextByMatch(String strategy, String text) throws SwipeException {
        mobileSteps.swipeToTextByMatch(strategy, text);
    }
}
