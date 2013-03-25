package webui.tests.pages;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import webui.tests.annotations.OnLoad;

import java.util.List;

/**
 * User: guym
 * Date: 3/6/13
 * Time: 5:31 PM
 *
 *
 * Since we are migrating from GWT login to a new login page we should support all modes possible modes.
 *
 */
@Component
public class LoginPage extends GsPage<LoginPage>{



    @Autowired
    private DashboardPage dashboardPage;

    @Autowired(required = false)
    private String rootUrl = "http://localhost:8099";

    private static Logger logger = LoggerFactory.getLogger( LoginPage.class );

    @Bean
    public LoginPage loginPage(){
        return new LoginPage();
    }

    @OnLoad
      @FindBy(css = "#username-input, input[name='username']")
      private List<WebElement> usernameInput;

    @OnLoad
    @FindBy(css = "body")
    private WebElement body;

      @OnLoad
      @FindBy(css = "#password-input, input[name='password']")
      private List<WebElement> passwordInput;

      @OnLoad
      @FindBy(css = "#login_button button, input[type='submit']")
      private List<WebElement> submit;


    public LoginPage gotoPage(){
        webDriver.get( rootUrl );
        load();
        return this;
    }

    public boolean isLoginWelcomeMessageVisible(){
        String bodyText = body.getText().toLowerCase();
        return bodyText.contains( "welcome" ) && bodyText.contains( "please log in" );
    }

    public void setRootUrl( String rootUrl ) {
        this.rootUrl = rootUrl;
    }

    private WebElement getVisible( List<WebElement> elements ){
        for ( WebElement element : elements )
        {
            if ( element.isDisplayed()){
                return element;
            }
        }
        throw new RuntimeException( String.format( "no visible element from list %s", elements ) );
    }

    public DashboardPage login( String username, String password ) {
          logger.info( String.format( "logging in with %s, %s", username, password ) );

          if ( !StringUtils.isEmpty( username ) )
          {
              getVisible( usernameInput ).sendKeys( username );
              logger.info( "typed username" );
          }
          if ( !StringUtils.isEmpty( password ) )
                {
                    getVisible( passwordInput ).sendKeys( password );
                    logger.info( "typed password" );
                }
          getVisible( submit ).click();
          return dashboardPage.load();
      }
}
