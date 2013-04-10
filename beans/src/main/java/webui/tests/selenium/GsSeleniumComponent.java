package webui.tests.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * User: guym
 * Date: 4/8/13
 * Time: 5:21 PM
 *
 * An interface to represent a Selenium component
 *
 */
public interface GsSeleniumComponent {

    public void setWebElement(WebElement webElement);

    public void setWebDriver( WebDriver webDriver );

}
