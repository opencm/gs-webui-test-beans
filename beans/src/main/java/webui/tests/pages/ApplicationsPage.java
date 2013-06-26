package webui.tests.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.stereotype.Component;
import webui.tests.components.AbstractComponent;
import webui.tests.components.FirstDisplayed;

/**
 * User: eliranm
 * Date: 6/25/13
 * Time: 12:57 PM
 */
@Component
public class ApplicationsPage extends GsPage<ApplicationsPage> {

/*
    @FirstDisplayed
    @FindBy(css =
            "#gs-tab-item-health," +
            "#gs-tab-item-physical," +
            "#gs-tab-item-logical," +
            "#gs-tab-item-logs," +
            "#gs-tab-item-topology-events-grid," +
            "#gs-tab-item-topology-events," +
            "#gs-tab-item-topology-recipes," +
            "#gs-tab-item-topology-cli")
    private AbstractComponent tabItem;
*/

    @FindBy(css = "#gs-tab-item-topology-cli-toggler-button")
    private WebElement installationProgressButton;

    @FindBy(css = "#gs-tab-item-topology-cli")
    private InstallationProgressTab installationProgressTab;

    public InstallationProgressTab installationProgress() {
        installationProgressButton.click();
        return installationProgressTab;
    }

    public static class InstallationProgressTab extends AbstractComponent<InstallationProgressTab> {

        @FindBy(css = "#gs-tab-item-topology-cli .x-panel-header")
        public WebElement recipePath;

        @FindBy(css = "#gs-tab-item-topology-cli pre")
        public WebElement console;

    }

}
