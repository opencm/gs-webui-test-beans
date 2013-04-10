package webui.tests.components;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * User: guym
 * Date: 4/10/13
 * Time: 1:46 PM
 *
 *
 * This base component exposes all the webElement methods by delegation.
 * It allows you to extend the webElement interface with whatever you want.
 *
 * A great use case is, for example - Select box!
 * You can simply extend this class and add more relevant interface such as
 *
 * public boolean hasOption( String str ){
 *      ...
 * }
 *
 */
public class SingleWebElementComponent<T extends SingleWebElementComponent> extends AbstractComponent<T> implements WebElement{
    @Override
    public void click() {
        webElement.click();
    }

    @Override
    public void submit() {
        webElement.submit();
    }

    @Override
    public void sendKeys( CharSequence... keysToSend ) {
         webElement.sendKeys( keysToSend );
    }

    @Override
    public void clear() {
        webElement.clear();
    }

    @Override
    public String getTagName() {
        return webElement.getTagName();
    }

    @Override
    public String getAttribute( String name ) {
        return webElement.getAttribute( name );
    }

    @Override
    public boolean isSelected() {
        return webElement.isSelected();
    }

    @Override
    public boolean isEnabled() {
        return webElement.isEnabled();
    }

    @Override
    public String getText() {
        return webElement.getText();
    }

    @Override
    public List<WebElement> findElements( By by ) {
        return webElement.findElements( by );
    }

    @Override
    public WebElement findElement( By by ) {
        return webElement.findElement( by );
    }

    @Override
    public boolean isDisplayed() {
        return webElement.isDisplayed();
    }

    @Override
    public Point getLocation() {
        return webElement.getLocation();
    }

    @Override
    public Dimension getSize() {
        return webElement.getSize();
    }

    @Override
    public String getCssValue( String propertyName ) {
        return webElement.getCssValue( propertyName );
    }
}
