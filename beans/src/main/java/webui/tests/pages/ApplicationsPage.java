package webui.tests.pages;

import com.google.common.base.Predicate;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import webui.tests.annotations.OnLoad;
import webui.tests.components.*;

import java.util.List;

/**
 * User: eliranm
 * Date: 6/25/13
 * Time: 12:57 PM
 */
@Component
public class ApplicationsPage extends GsPage<ApplicationsPage> {

    public static final String DEFAULT_APPLICATION = "default";

    @OnLoad
    @FindBy(id = "gs-graph-application-map")
    private ApplicationMap applicationMap;

    @FindBy(className = "gs-inner-tabs-container")
    private ApplicationsInnerTabs innerTabs;

    @OnLoad
    @FindBy(id = "gs-button-uninstall-app")
    private WebElement uninstallApplicationButton;

    @OnLoad
    @FindBy(id = "gs-application-combo-TOPOLOGY")
    private ComboBox comboBox;

    // using FirstDisplayed as failsafe: if element is not matched,
    // the field will be populated with null value
    @FirstDisplayed
    @FindBy(css = "#gs-tab-item-topology-progress")
    private ProgressTab progressTab;

    public ApplicationsPage progressTab() {
        innerTabs.to(ApplicationsInnerTabs.PROGRESS);
        return this;
    }

    public ApplicationsPage selectApplication(String name) {
        assert comboBox.has(name) : "application not found in combobox, cannot select it";
        comboBox.select(name);
        return this;
    }

    public String getSelectedApplication() {
        return comboBox.active();
    }

    public List<String> listApplications() {
        return comboBox.items();
    }

    /**
     * <p>
     * Gets the services deployed with no specified application, i.e. on
     * the "default" application.
     * </p>
     * <p>
     * This method must switch to the "default" application if it is not
     * selected in order to list the services, when it's finished, it will
     * switch back to the previously selected application.
     * </p>
     *
     * @return A list of service names belong to the "default" application.
     */
    public List<String> listDefaultServices() {
        String currApplication = getSelectedApplication();
        boolean switched = false;
        if (shouldSwitchApplication(currApplication, DEFAULT_APPLICATION)) {
            selectApplication(DEFAULT_APPLICATION);
            switched = true;
        }
        List<String> services = listServices();
        if (switched) {
            selectApplication(currApplication);
        }
        return services;
    }

    /**
     * Gets the services deployed on the current application.
     *
     * @return A list of service names.
     */
    public List<String> listServices() {
        return applicationMap.names();
    }

    /**
     * Uninstalls the currently selected application.
     *
     * @return this (chainable).
     */
    public ApplicationsPage uninstallApplication() {
        uninstallApplicationButton.click();
        return this;
    }

    /**
     * Uninstalls the specified application. If the application is not
     * currently selected, this method will fail silently.
     *
     * @param name The application name.
     * @return this (chainable).
     */
    public ApplicationsPage uninstallApplication(String name) {
        if (getSelectedApplication().equals(name)) {
            uninstallApplication();
        }
        return this;
    }

    /**
     * Uninstalls the specified service from the "default" application.
     * If the "default" application is not currently selected, it will be
     * selected before the uninstall.
     *
     * @param name The service name to uninstall.
     * @return this (chainable).
     */
    public ApplicationsPage uninstallDefaultService(String name) {
        String currApplication = getSelectedApplication();
        boolean switched = false;
        if (shouldSwitchApplication(currApplication, DEFAULT_APPLICATION)) {
            selectApplication(DEFAULT_APPLICATION);
            switched = true;
        }
        uninstallService(name);
        if (switched) {
            selectApplication(currApplication);
        }
        return this;
    }

    private boolean shouldSwitchApplication(String from, String to) {
        return listApplications().contains(to) && !to.equals(from);
    }

    /**
     * Uninstalls the specified service from the current application.
     * If the service is not deployed in the current application,
     * this method will fail silently.
     *
     * @param name The service name.
     * @return this (chainable).
     */
    public ApplicationsPage uninstallService(String name) {
        if (applicationMap.has(name)) {
            applicationMap.uninstall(name);
        }
        return this;
    }

    /**
     * Waits for a service to disappear from the application. Use this method
     * after uninstalling a service to make sure it is no longer deployed.
     *
     * @param name The service name.
     * @return this (chainable).
     */
    public ApplicationsPage waitForServiceUninstall(final String name) {
        new WebDriverWait(webDriver, 20).until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver webDriver) {
                return !applicationMap.has(name);
            }
        });
        return this;
    }

    public String progressText() {
        assert progressTab != null : "cannot get progress text when progress tab is not selected";
        return progressTab.console.getText();
    }

    public String progressPath() {
        assert progressTab != null : "cannot get progress path when progress tab is not selected";
        return progressTab.recipePath.getText();
    }


    public static class ProgressTab extends AbstractComponent<ProgressTab> {

        @FindBy(css = ".x-panel-header")
        public WebElement recipePath;

        @FindBy(css = "pre")
        public WebElement console;

    }

}
