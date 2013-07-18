package webui.tests.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import webui.tests.annotations.OnLoad;

/**
 * User: guym
 * Date: 3/7/13
 * Time: 8:16 AM
 */
@Component
public class DashboardPage extends GsPage<DashboardPage> {


    @OnLoad
    @FindBy(id = "gs-tab-item-recipes-button")
    private WebElement recipesButton;

    @OnLoad
    @FindBy(id = "gs-tab-item-topology-button")
    private WebElement applicationsButton;

    @Autowired
    private RecipesRepositoryPage recipesRepositoryPage;

    @Autowired
    private ApplicationsPage applicationsPage;

    public ApplicationsPage gotoApplications() {
        applicationsButton.click();
        applicationsPage.load();
        return applicationsPage;
    }

    public RecipesRepositoryPage gotoRecipes() {
        recipesButton.click();
        recipesRepositoryPage.load();
        return recipesRepositoryPage;
    }


    @OnLoad
    @FindBy(css="#gs-about-button")
    private WebElement aboutButton;

    @Autowired
    private ComplexLoginPage loginPage;

    @OnLoad
    @FindBy(css="#gs-logout-button")
    private WebElement logoutButton;

    @Bean
    public DashboardPage dashboardPage(){
        return new DashboardPage();
    }

    public WebElement getAboutButton() {
        return aboutButton;
    }

    public DashboardPage clickLogout(){
        logoutButton.click();
        return this;
    }

    public ComplexLoginPage logout(){
        logoutButton.click();
        closeDialog( "yes" );
        return loginPage.load();
    }


    public void setLoginPage( ComplexLoginPage loginPage ) {
        this.loginPage = loginPage;
    }
}
