package pagefactory;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(monochrome = false, plugin = {"pretty"},
        glue = {"ru.sbtqa.tag.stepdefs",  "setting"},
        features = {"src/test/resources/features"},
        tags = {"@data-fragment"}
)
public class CucumberTest {}