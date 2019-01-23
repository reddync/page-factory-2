package ru.sbtqa.tag.stepdefs.en;

import cucumber.api.java.en.And;
import ru.sbtqa.tag.pagefactory.exceptions.SwipeException;
import ru.sbtqa.tag.stepdefs.MobileSteps;

public class MobileStepDefs extends MobileSteps {

    /**
     * {@inheritDoc}
     */
    @Override
    @And("^user swipes in direction \"(.*?)\" to the text \"(.*?)\"$")
    public void swipeToTextByDirection(String direction, String text) throws SwipeException {
        super.swipeToTextByDirection(direction, text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @And("^user swipes with match strategy \"(.*?)\" to the text \"(.*?)\"$")
    public void swipeToTextByMatch(String strategy, String text) throws SwipeException {
        super.swipeToTextByMatch(strategy, text);
    }
}
