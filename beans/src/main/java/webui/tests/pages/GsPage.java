package webui.tests.pages;

import com.thoughtworks.selenium.Selenium;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webui.tests.components.AbstractComponent;
import webui.tests.utils.CollectionUtils;

import java.util.Collection;
import java.util.List;

/**
 * User: guym
 * Date: 3/6/13
 * Time: 10:38 PM
 */
@Component
public abstract class GsPage<T extends GsPage> extends AbstractComponent<T> {

    @Autowired
    protected Selenium selenium;

    public boolean isTextPresent( String text ) {
        return selenium.isTextPresent( text );
    }

    @Autowired
    public GsPage<T> setSelenium( Selenium selenium ) {
        this.selenium = selenium;
        return this;
    }

    public Collection<WebElement> findDisplayedWindowDialogs() {
        return findDisplayed( By.cssSelector( ".x-window-dlg" ) );
    }

    public Collection<WebElement> findDisplayed( By by ) {
        List<WebElement> elements = webDriver.findElements( by );
        Collection<WebElement> displayElements = CollectionUtils.select( elements, new Predicate() {
            @Override
            public boolean evaluate( Object o ) {
                return ( ( WebElement ) o ).isDisplayed();
            }
        } );
        return displayElements;
    }

    public T load() {
        load( webDriver );
        return ( T ) this;
    }


    public boolean isTextInPopup( String containedText ) {
        Collection<WebElement> popups = findDisplayedWindowDialogs();
        if ( !CollectionUtils.isEmpty( popups ) )
        {
            for ( WebElement popup : popups )
            {
                String text = popup.getText();
                if ( !StringUtils.isEmpty( text ) && text.toLowerCase(  ).contains( containedText.toLowerCase(  ) ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    public T closeDialog( String str ){
        WebElement e = CollectionUtils.first( findDisplayedWindowDialogs() );
        List<WebElement> buttons = e.findElements( By.cssSelector( "button" ) );
        for ( WebElement button  : buttons )
        {
            if ( button.getText().equalsIgnoreCase( str )){
                button.click();
                break;
            }
        }
        return (T) this;
    }

}
