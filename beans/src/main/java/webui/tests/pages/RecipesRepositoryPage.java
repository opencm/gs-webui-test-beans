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
import webui.tests.components.RecipesInnerTabs;

/**
 * User: eliranm
 * Date: 6/25/13
 * Time: 12:57 PM
 */
@Component
public class RecipesRepositoryPage extends GsPage<RecipesRepositoryPage> {

    private static Logger logger = LoggerFactory.getLogger(RecipesRepositoryPage.class);

    @FindBy(xpath = "//*[@id=\"gs-tab-item-recipes\"]/div/div[2]/div[1]/div")
    private RecipesInnerTabs innerTabs;

    @FirstDisplayed
    @FindBy(css = "#gs-recipes-repo-apps,#gs-recipes-repo-services")
    private RepositoryBrowser repositoryBrowser;

    public RecipesRepositoryPage applications() {
        innerTabs.to(RecipesInnerTabs.APPLICATIONS);
        return this;
    }

    public RecipesRepositoryPage services() {
        innerTabs.to(RecipesInnerTabs.SERVICES);
        return this;
    }

    @Bean
    public RecipesRepositoryPage recipesRepositoryPage() {
        return new RecipesRepositoryPage();
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
                    // example matches: id="gs-slider-grid-apps_computers", id="gs-slider-grid-services_tomcat"
                    ExpectedConditions.elementToBeClickable(By.cssSelector("div[id^='gs-slider-grid-'][id$='" + recipeName + "']")));
            if (row != null) {
                row.findElement(By.cssSelector(".x-grid3-col-install button")).click();
            }
        }
    }

}
