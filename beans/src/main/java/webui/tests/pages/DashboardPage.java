package webui.tests.pages;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import webui.tests.annotations.OnLoad;

import java.util.Collection;

/**
 * User: guym
 * Date: 3/7/13
 * Time: 8:16 AM
 */
@Component
public class DashboardPage extends GsPage<DashboardPage> {

    @OnLoad
    @FindBy(css="#gs-about-button")
    private WebElement aboutButton;

    @Autowired
    private LoginPage loginPage;

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

    public LoginPage logout(){
        logoutButton.click();
        closeDialog( "yes" );
        return loginPage.load();
    }


    public void setLoginPage( LoginPage loginPage ) {
        this.loginPage = loginPage;
    }
}
