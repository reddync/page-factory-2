package ru.sbtqa.tag.pagefactory.pages.htmlelements.IndexPages;

import org.openqa.selenium.support.FindBy;
import ru.sbtqa.tag.pagefactory.HTMLPage;
import ru.sbtqa.tag.pagefactory.annotations.ElementTitle;
import ru.sbtqa.tag.pagefactory.annotations.PageEntry;
import ru.sbtqa.tag.pagefactory.pages.htmlelements.blocks.MenuBlock;
import ru.yandex.qatools.htmlelements.element.TextBlock;

@PageEntry(title = "MainY")
public class IndexPage extends HTMLPage {

    @ElementTitle(value = "menu")
    public MenuBlock menu;

    @ElementTitle("Sample text")
    @FindBy(xpath = "//*[@id='samples']/p[@class='lead']")
    public TextBlock sampleText;

    @ElementTitle("License text")
    @FindBy(xpath = "//*[@id='license']/p[@class='lead']")
    public TextBlock licenseText;
}
