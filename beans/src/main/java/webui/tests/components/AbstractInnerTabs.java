package webui.tests.components;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.stereotype.Component;
import webui.tests.annotations.OnLoad;

import java.util.List;

/**
 * User: eliranm
 * Date: 7/1/13
 * Time: 11:05 AM
 *
 * A generic representation for inner tabs.
 */
@Component
public abstract class AbstractInnerTabs<T> extends AbstractComponent<AbstractInnerTabs> {

    // all tab headers
    @OnLoad
    @FindBy(css = ".gs-inner-tabs-toggler")
    private List<WebElement> headers;

    // the currently displayed tab item
    @FirstDisplayed
    @FindBy(css = ".gs-tab-item")
    private WebElement activeTab;

    /**
     * Switches to the desired tab, provided its name as appears in
     * the tab header text.
     *
     * @param tabName The tab name as appears in the tab header.
     * @return this
     */
    public T to(String tabName) {
        for (WebElement h : headers) {
            if (h.getText().equalsIgnoreCase(tabName)) {
                h.click();
                break;
            }
        }
        return (T) this;
    }

    /**
     * Gets the currently active (visible) tab item.
     *
     * @return The currently active tab item.
     */
    public WebElement activeTab() {
        return activeTab;
    }

}
