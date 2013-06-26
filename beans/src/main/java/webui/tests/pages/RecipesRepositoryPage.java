package webui.tests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import webui.tests.components.AbstractComponent;
import webui.tests.components.FirstDisplayed;

/**
 * User: eliranm
 * Date: 6/25/13
 * Time: 12:57 PM
 */
@Component
public class RecipesRepositoryPage extends GsPage<RecipesRepositoryPage> {

    private static Logger logger = LoggerFactory.getLogger(RecipesRepositoryPage.class);


    // this pattern is used to detect components that are hidden on load,
    // e.g. in a tab-panel structure. each time we click a different button,
    // and another tab item is visible, so its selector is returned from
    // the @FirstDisplayed annotated element reference.
    @FindBy(id = "gs-tab-item-recipes-repo-apps-toggler-button")
    private WebElement applicationsButton;
    @FindBy(id = "gs-tab-item-recipes-repo-services-toggler-button")
    private WebElement servicesButton;
    @FirstDisplayed
    @FindBy(css = "#gs-recipes-repo-apps,#gs-recipes-repo-services")
    private RepositoryBrowser repositoryBrowser;

    @Bean
    public RecipesRepositoryPage recipesRepositoryPage() {
        return new RecipesRepositoryPage();
    }

    public RecipesRepositoryPage applications() {
        applicationsButton.click();
        return this;
    }

    public RecipesRepositoryPage services() {
        servicesButton.click();
        return this;
    }

    public RecipesRepositoryPage install(String recipeName) {
        repositoryBrowser.install(recipeName);
        return this;
    }

    public static class RepositoryBrowser extends AbstractComponent<RepositoryBrowser> {

        @FindBy(className = "gs-slider-breadcrumb")
        private WebElement path;

        public void install(String recipeName) {

            logger.info("waiting for row element");
            WebElement row = (new WebDriverWait(webDriver, 15)).until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector("#gs-slider-grid-apps_" + recipeName)));
            if (row != null) {
                row.findElement(By.cssSelector(".x-grid3-col-install button")).click();
            }
        }
    }

}
